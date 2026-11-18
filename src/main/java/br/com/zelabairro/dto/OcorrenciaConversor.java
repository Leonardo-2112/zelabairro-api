package br.com.zelabairro.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.zelabairro.model.Ocorrencia;

//Converte o model Ocorrencia no DTO de saida. O ModelMapper copia os campos de mesmo nome
//(id, descricao, status, datas, categoria, bairro, qtdApoios, diasEmAberto, atrasada, prioridade...);
//aqui completamos o que tem nome diferente ou regra propria.
@Component
public class OcorrenciaConversor {

    private final ModelMapper mapper;

    public OcorrenciaConversor(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public OcorrenciaResponseDto paraDto(Ocorrencia ocorrencia) {
        OcorrenciaResponseDto dto = mapper.map(ocorrencia, OcorrenciaResponseDto.class);

        //RN13: so o primeiro nome do autor, nunca e-mail ou telefone
        dto.setIdAutor(ocorrencia.getCidadao().getId());
        dto.setAutor(ocorrencia.getCidadao().getPrimeiroNome());

        //Categoria: nome do orgao responsavel
        dto.getCategoria().setIdOrgao(ocorrencia.getCategoria().getOrgao().getId());
        dto.getCategoria().setOrgao(ocorrencia.getCategoria().getOrgao().getNome());

        //Previsao da IA (nula se a IA estava fora do ar - RN12). D4: risco ALTO/BAIXO, sem porcentagem
        dto.setPrazoPrevistoDias(ocorrencia.getPrazoPrevisto());
        if (ocorrencia.getRiscoAtraso() == null) {
            dto.setRiscoAtraso(null);
        } else {
            dto.setRiscoAtraso("S".equals(ocorrencia.getRiscoAtraso()) ? "ALTO" : "BAIXO");
        }

        //RN10: prioridade calculada na hora (null para RESOLVIDA/CANCELADA)
        dto.setPontuacaoPrioridade(ocorrencia.getPontuacaoPrioridade());
        dto.setPrioridade(ocorrencia.getPrioridade() != null ? ocorrencia.getPrioridade().name() : null);
        dto.setStatus(ocorrencia.getStatus().name());
        return dto;
    }
}
