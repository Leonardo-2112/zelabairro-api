package br.com.zelabairro.bo;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.GestorDao;
import br.com.zelabairro.exception.AcessoNegadoException;
import br.com.zelabairro.model.Ocorrencia;

//Confere quem esta chamando: gestor (RN16) ou autor da ocorrencia (RN18)
@Service
public class AcessoBo {

    public static final String HEADER_GESTOR = "X-Gestor-Id";
    public static final String HEADER_CIDADAO = "X-Cidadao-Id";

    private final GestorDao gestorDao;

    public AcessoBo(GestorDao gestorDao) {
        this.gestorDao = gestorDao;
    }

    //RN16: a cada requisicao, o id do cabecalho X-Gestor-Id precisa existir em tb_gestor
    public void validarGestor(Integer idGestor) throws SQLException {
        if (idGestor == null || !gestorDao.existe(idGestor)) {
            throw new AcessoNegadoException("RN16: rota exclusiva de gestor. Envie um X-Gestor-Id válido.");
        }
    }

    //RN18: so o autor da ocorrencia (cabecalho X-Cidadao-Id) envia ou remove fotos
    public void validarAutor(Integer idCidadao, Ocorrencia ocorrencia) {
        if (idCidadao == null || idCidadao != ocorrencia.getCidadao().getId()) {
            throw new AcessoNegadoException("RN18: só o autor da ocorrência pode enviar ou remover fotos.");
        }
    }
}
