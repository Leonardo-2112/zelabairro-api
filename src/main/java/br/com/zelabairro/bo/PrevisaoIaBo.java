package br.com.zelabairro.bo;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.zelabairro.model.Ocorrencia;

//Chama a API de IA (Flask) UMA vez, ao criar a ocorrencia (Documento Base 7.4).
//Se a IA estiver fora do ar, demorar ou nao estiver configurada, a ocorrencia nasce sem previsao (RN12).
@Service
public class PrevisaoIaBo {

    private static final Logger log = LoggerFactory.getLogger(PrevisaoIaBo.class);

    //D3: o SP156 nao tem chamados de iluminacao no treino, entao a previsao nao teria base
    private static final String CATEGORIA_SEM_BASE = "Iluminação pública apagada";

    private final RestClient restClient; //null quando IA_API_URL esta vazia

    public PrevisaoIaBo(@Value("${app.ia.url}") String urlIa, @Value("${app.ia.timeout-ms}") int timeoutMs) {
        if (urlIa == null || urlIa.isBlank()) {
            this.restClient = null;
            return;
        }
        //Timeout curto: a IA nunca pode travar o cadastro do cidadao
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeoutMs)).build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(httpClient);
        fabrica.setReadTimeout(Duration.ofMillis(timeoutMs));
        this.restClient = RestClient.builder()
                .baseUrl(urlIa.endsWith("/") ? urlIa.substring(0, urlIa.length() - 1) : urlIa)
                .requestFactory(fabrica)
                .build();
    }

    //Preenche prazoPrevisto e riscoAtraso da ocorrencia (ou deixa nulos)
    public void preencherPrevisao(Ocorrencia ocorrencia) {
        ocorrencia.setPrazoPrevisto(null);
        ocorrencia.setRiscoAtraso(null);
        if (restClient == null) return;                                                    //RN12: IA nao configurada
        if (CATEGORIA_SEM_BASE.equalsIgnoreCase(ocorrencia.getCategoria().getNome())) return; //D3

        //Corpo do contrato da secao 7.3: {categoria, subprefeitura, mes, dia_semana}
        LocalDate hoje = ocorrencia.getDataAbertura();
        Map<String, Object> corpo = Map.of(
                "categoria", ocorrencia.getCategoria().getNome(),
                "subprefeitura", ocorrencia.getBairro().getSubprefeitura(),
                "mes", hoje.getMonthValue(),
                "dia_semana", hoje.getDayOfWeek().getValue() - 1); //0 = segunda ... 6 = domingo

        try {
            Map<?, ?> prazo = chamar("/prever/prazo", corpo);
            Map<?, ?> risco = chamar("/prever/atraso", corpo);
            ocorrencia.setPrazoPrevisto(((Number) prazo.get("prazo_previsto_dias")).intValue());
            String flag = String.valueOf(risco.get("risco_atraso"));
            ocorrencia.setRiscoAtraso("S".equals(flag) ? "S" : "N");
        } catch (Exception e) {
            //RN12: qualquer falha (fora do ar, timeout, 400, resposta estranha) = sem previsao
            log.warn("API de IA indisponivel, ocorrencia criada sem previsao (RN12): {}", e.getMessage());
            ocorrencia.setPrazoPrevisto(null);
            ocorrencia.setRiscoAtraso(null);
        }
    }

    private Map<?, ?> chamar(String rota, Map<String, Object> corpo) {
        return restClient.post()
                .uri(rota)
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .retrieve()
                .body(Map.class);
    }
}
