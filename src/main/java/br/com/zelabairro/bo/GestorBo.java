package br.com.zelabairro.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.CidadaoDao;
import br.com.zelabairro.dao.GestorDao;
import br.com.zelabairro.dao.OrgaoDao;
import br.com.zelabairro.exception.ConflitoException;
import br.com.zelabairro.model.Gestor;

//Regras do gestor: so outro gestor cadastra (RN16, RN17), e-mail unico (RN02), senha so como hash (RN15)
@Service
public class GestorBo {

    private final GestorDao gestorDao;
    private final CidadaoDao cidadaoDao;
    private final OrgaoDao orgaoDao;
    private final AcessoBo acessoBo;
    private final BCryptPasswordEncoder encoder;

    public GestorBo(GestorDao gestorDao, CidadaoDao cidadaoDao, OrgaoDao orgaoDao, AcessoBo acessoBo, BCryptPasswordEncoder encoder) {
        this.gestorDao = gestorDao;
        this.cidadaoDao = cidadaoDao;
        this.orgaoDao = orgaoDao;
        this.acessoBo = acessoBo;
        this.encoder = encoder;
    }

    public List<Gestor> listar(Integer idGestorLogado) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        return gestorDao.listar();
    }

    public Gestor buscar(Integer idGestorLogado, int id) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        return gestorDao.buscarPorId(id);
    }

    //RN17: quem cadastra fica registrado em cd_gestor_cadastro
    public Gestor cadastrar(Integer idGestorLogado, Gestor gestor) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        orgaoDao.buscarPorId(gestor.getOrgao().getId()); //404 se o orgao nao existir
        validarEmail(gestor.getEmail(), 0);
        gestor.setSenha(encoder.encode(gestor.getSenha()));
        gestor.setIdGestorCadastro(idGestorLogado);
        gestorDao.cadastrar(gestor);
        return gestorDao.buscarPorId(gestor.getId());
    }

    public Gestor atualizar(Integer idGestorLogado, Gestor gestor) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        gestorDao.buscarPorId(gestor.getId());
        orgaoDao.buscarPorId(gestor.getOrgao().getId());
        validarEmail(gestor.getEmail(), gestor.getId());
        gestor.setSenha(encoder.encode(gestor.getSenha()));
        gestorDao.atualizar(gestor);
        return gestorDao.buscarPorId(gestor.getId());
    }

    public void remover(Integer idGestorLogado, int id) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        gestorDao.buscarPorId(id);
        if (gestorDao.contarVinculos(id) > 0) {
            throw new ConflitoException("O gestor tem atualizações ou gestores cadastrados por ele e não pode ser removido.");
        }
        gestorDao.remover(id);
    }

    //RN02: e-mail unico, e o mesmo e-mail nao pode existir como cidadao (secao 6.2)
    private void validarEmail(String email, int idIgnorar) throws SQLException {
        if (gestorDao.existeEmail(email, idIgnorar) || cidadaoDao.existeEmail(email, 0)) {
            throw new ConflitoException("RN02: este e-mail já está cadastrado.");
        }
    }
}
