package br.com.zelabairro.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.CategoriaDao;
import br.com.zelabairro.dao.OrgaoDao;
import br.com.zelabairro.exception.ConflitoException;
import br.com.zelabairro.exception.RegraNegocioException;
import br.com.zelabairro.model.Categoria;

//Regras da categoria: nome unico, orgao existente e categoria com ocorrencias nao e excluida (RN14)
@Service
public class CategoriaBo {

    private final CategoriaDao categoriaDao;
    private final OrgaoDao orgaoDao;

    public CategoriaBo(CategoriaDao categoriaDao, OrgaoDao orgaoDao) {
        this.categoriaDao = categoriaDao;
        this.orgaoDao = orgaoDao;
    }

    public List<Categoria> listar() throws SQLException {
        return categoriaDao.listar();
    }

    public Categoria buscar(int id) throws SQLException {
        return categoriaDao.buscarPorId(id);
    }

    public Categoria cadastrar(Categoria categoria) throws SQLException {
        orgaoDao.buscarPorId(categoria.getOrgao().getId()); //404 se o orgao nao existir
        if (categoriaDao.existeNome(categoria.getNome(), 0)) {
            throw new ConflitoException("Já existe uma categoria com esse nome.");
        }
        categoriaDao.cadastrar(categoria);
        return categoriaDao.buscarPorId(categoria.getId());
    }

    public Categoria atualizar(Categoria categoria) throws SQLException {
        categoriaDao.buscarPorId(categoria.getId());
        orgaoDao.buscarPorId(categoria.getOrgao().getId());
        if (categoriaDao.existeNome(categoria.getNome(), categoria.getId())) {
            throw new ConflitoException("Já existe uma categoria com esse nome.");
        }
        categoriaDao.atualizar(categoria);
        return categoriaDao.buscarPorId(categoria.getId());
    }

    public void remover(int id) throws SQLException {
        categoriaDao.buscarPorId(id);
        if (categoriaDao.contarOcorrencias(id) > 0) {
            throw new RegraNegocioException("RN14: a categoria tem ocorrências vinculadas e não pode ser excluída.");
        }
        categoriaDao.remover(id);
    }
}
