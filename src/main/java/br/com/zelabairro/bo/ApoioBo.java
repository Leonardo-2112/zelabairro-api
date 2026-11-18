package br.com.zelabairro.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.ApoioDao;
import br.com.zelabairro.dao.CidadaoDao;
import br.com.zelabairro.dao.OcorrenciaDao;
import br.com.zelabairro.exception.RegraNegocioException;
import br.com.zelabairro.model.Apoio;
import br.com.zelabairro.model.Cidadao;
import br.com.zelabairro.model.Ocorrencia;

//Apoios (N:N cidadao <-> ocorrencia): RN08
@Service
public class ApoioBo {

    private final ApoioDao apoioDao;
    private final OcorrenciaDao ocorrenciaDao;
    private final CidadaoDao cidadaoDao;

    public ApoioBo(ApoioDao apoioDao, OcorrenciaDao ocorrenciaDao, CidadaoDao cidadaoDao) {
        this.apoioDao = apoioDao;
        this.ocorrenciaDao = ocorrenciaDao;
        this.cidadaoDao = cidadaoDao;
    }

    public List<Apoio> listar(int idOcorrencia) throws SQLException {
        ocorrenciaDao.buscarPorId(idOcorrencia);
        return apoioDao.listarPorOcorrencia(idOcorrencia);
    }

    public Apoio buscar(int idOcorrencia, int idCidadao) throws SQLException {
        ocorrenciaDao.buscarPorId(idOcorrencia);
        return apoioDao.buscar(idOcorrencia, idCidadao);
    }

    public Apoio cadastrar(int idOcorrencia, int idCidadao) throws SQLException {
        Ocorrencia ocorrencia = ocorrenciaDao.buscarPorId(idOcorrencia); //404
        cidadaoDao.buscarPorId(idCidadao);                               //404

        //RN08: nao apoia a propria, so ABERTA/EM_ANDAMENTO e no maximo uma vez
        if (ocorrencia.getCidadao().getId() == idCidadao) {
            throw new RegraNegocioException("RN08: o cidadão não pode apoiar a própria ocorrência.");
        }
        if (!ocorrencia.getStatus().isEmAberto()) {
            throw new RegraNegocioException("RN08: só é possível apoiar ocorrências ABERTA ou EM_ANDAMENTO.");
        }
        if (apoioDao.existe(idOcorrencia, idCidadao)) {
            throw new RegraNegocioException("RN08: o cidadão já apoia esta ocorrência.");
        }

        Apoio apoio = new Apoio();
        apoio.setIdOcorrencia(idOcorrencia);
        apoio.setCidadao(new Cidadao(idCidadao));
        apoioDao.cadastrar(apoio);
        return apoioDao.buscar(idOcorrencia, idCidadao);
    }

    public void remover(int idOcorrencia, int idCidadao) throws SQLException {
        ocorrenciaDao.buscarPorId(idOcorrencia);
        apoioDao.remover(idOcorrencia, idCidadao); //404 se o apoio nao existir
    }
}
