package br.com.zelabairro.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.BairroDao;
import br.com.zelabairro.exception.ConflitoException;
import br.com.zelabairro.exception.RegraNegocioException;
import br.com.zelabairro.model.Bairro;

//Regras do bairro: nome + subprefeitura unicos, e bairro com ocorrencias nao e excluido (RN14)
@Service
public class BairroBo {

    private final BairroDao bairroDao;

    public BairroBo(BairroDao bairroDao) {
        this.bairroDao = bairroDao;
    }

    public List<Bairro> listar() throws SQLException {
        return bairroDao.listar();
    }

    public Bairro buscar(int id) throws SQLException {
        return bairroDao.buscarPorId(id);
    }

    public Bairro cadastrar(Bairro bairro) throws SQLException {
        if (bairroDao.existe(bairro.getNome(), bairro.getSubprefeitura(), 0)) {
            throw new ConflitoException("Já existe um bairro com esse nome nessa subprefeitura.");
        }
        bairroDao.cadastrar(bairro);
        return bairro;
    }

    public Bairro atualizar(Bairro bairro) throws SQLException {
        bairroDao.buscarPorId(bairro.getId());
        if (bairroDao.existe(bairro.getNome(), bairro.getSubprefeitura(), bairro.getId())) {
            throw new ConflitoException("Já existe um bairro com esse nome nessa subprefeitura.");
        }
        bairroDao.atualizar(bairro);
        return bairro;
    }

    public void remover(int id) throws SQLException {
        bairroDao.buscarPorId(id);
        if (bairroDao.contarOcorrencias(id) > 0) {
            throw new RegraNegocioException("RN14: o bairro tem ocorrências vinculadas e não pode ser excluído.");
        }
        bairroDao.remover(id);
    }
}
