package br.com.zelabairro.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.CidadaoDao;
import br.com.zelabairro.dao.GestorDao;
import br.com.zelabairro.exception.ConflitoException;
import br.com.zelabairro.model.Cidadao;

//Regras do cidadao: e-mail unico (RN02), senha so como hash (RN15), remocao sem vinculos
@Service
public class CidadaoBo {

    private final CidadaoDao cidadaoDao;
    private final GestorDao gestorDao;
    private final BCryptPasswordEncoder encoder;

    public CidadaoBo(CidadaoDao cidadaoDao, GestorDao gestorDao, BCryptPasswordEncoder encoder) {
        this.cidadaoDao = cidadaoDao;
        this.gestorDao = gestorDao;
        this.encoder = encoder;
    }

    public List<Cidadao> listar() throws SQLException {
        return cidadaoDao.listar();
    }

    public Cidadao buscar(int id) throws SQLException {
        return cidadaoDao.buscarPorId(id);
    }

    //RN17: o cadastro publico sempre cria cidadao
    public Cidadao cadastrar(Cidadao cidadao) throws SQLException {
        validarEmail(cidadao.getEmail(), 0);
        cidadao.setSenha(encoder.encode(cidadao.getSenha())); //RN15: grava so o hash
        cidadaoDao.cadastrar(cidadao);
        return cidadaoDao.buscarPorId(cidadao.getId());
    }

    public Cidadao atualizar(Cidadao cidadao) throws SQLException {
        cidadaoDao.buscarPorId(cidadao.getId()); //404 se nao existir
        validarEmail(cidadao.getEmail(), cidadao.getId());
        cidadao.setSenha(encoder.encode(cidadao.getSenha()));
        cidadaoDao.atualizar(cidadao);
        return cidadaoDao.buscarPorId(cidadao.getId());
    }

    public void remover(int id) throws SQLException {
        cidadaoDao.buscarPorId(id);
        if (cidadaoDao.contarVinculos(id) > 0) {
            throw new ConflitoException("O cidadão tem ocorrências ou apoios registrados e não pode ser removido.");
        }
        cidadaoDao.remover(id);
    }

    //RN02: e-mail unico, e o mesmo e-mail nao pode existir como gestor (secao 6.2)
    private void validarEmail(String email, int idIgnorar) throws SQLException {
        if (cidadaoDao.existeEmail(email, idIgnorar) || gestorDao.existeEmail(email, 0)) {
            throw new ConflitoException("RN02: este e-mail já está cadastrado.");
        }
    }
}
