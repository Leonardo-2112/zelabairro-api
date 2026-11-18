package br.com.zelabairro.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.BairroDao;
import br.com.zelabairro.dao.OcorrenciaDao;
import br.com.zelabairro.dto.PainelBairroDto;
import br.com.zelabairro.model.Bairro;
import br.com.zelabairro.model.Ocorrencia;

//Painel do gestor (RN16): quantas ocorrencias cada bairro tem em cada status e quantas estao atrasadas (RN11)
@Service
public class PainelBo {

    private final BairroDao bairroDao;
    private final OcorrenciaDao ocorrenciaDao;
    private final AcessoBo acessoBo;

    public PainelBo(BairroDao bairroDao, OcorrenciaDao ocorrenciaDao, AcessoBo acessoBo) {
        this.bairroDao = bairroDao;
        this.ocorrenciaDao = ocorrenciaDao;
        this.acessoBo = acessoBo;
    }

    public List<PainelBairroDto> porBairro(Integer idGestorLogado) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        List<Ocorrencia> ocorrencias = ocorrenciaDao.listar(null, null, null);

        List<PainelBairroDto> painel = new ArrayList<>();
        for (Bairro bairro : bairroDao.listar()) {
            int abertas = 0, emAndamento = 0, resolvidas = 0, canceladas = 0, atrasadas = 0;
            for (Ocorrencia o : ocorrencias) {
                if (o.getBairro().getId() != bairro.getId()) continue;
                switch (o.getStatus()) {
                    case ABERTA -> abertas++;
                    case EM_ANDAMENTO -> emAndamento++;
                    case RESOLVIDA -> resolvidas++;
                    case CANCELADA -> canceladas++;
                }
                //Atrasada que ainda nao foi resolvida (o que o gestor precisa atacar)
                if (o.getStatus().isEmAberto() && o.isAtrasada()) atrasadas++;
            }
            PainelBairroDto linha = new PainelBairroDto();
            linha.setIdBairro(bairro.getId());
            linha.setBairro(bairro.getNome());
            linha.setSubprefeitura(bairro.getSubprefeitura());
            linha.setAbertas(abertas);
            linha.setEmAndamento(emAndamento);
            linha.setResolvidas(resolvidas);
            linha.setCanceladas(canceladas);
            linha.setAtrasadas(atrasadas);
            linha.setTotal(abertas + emAndamento + resolvidas + canceladas);
            painel.add(linha);
        }
        //Bairros com mais ocorrencias em aberto primeiro
        painel.sort((a, b) -> (b.getAbertas() + b.getEmAndamento()) - (a.getAbertas() + a.getEmAndamento()));
        return painel;
    }
}
