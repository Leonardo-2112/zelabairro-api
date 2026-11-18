package br.com.zelabairro.bo;

import java.sql.SQLException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.CidadaoDao;
import br.com.zelabairro.dao.GestorDao;
import br.com.zelabairro.dto.LoginResponseDto;
import br.com.zelabairro.exception.CredenciaisInvalidasException;
import br.com.zelabairro.model.Cidadao;
import br.com.zelabairro.model.Gestor;
import br.com.zelabairro.model.enums.Perfil;

//Login (RN15): procura o e-mail em tb_gestor e depois em tb_cidadao e compara o hash BCrypt
@Service
public class AuthBo {

    //Mensagem generica: nao conta se o erro foi no e-mail ou na senha
    private static final String MENSAGEM_401 = "RN15: e-mail ou senha inválidos.";

    private final GestorDao gestorDao;
    private final CidadaoDao cidadaoDao;
    private final BCryptPasswordEncoder encoder;

    public AuthBo(GestorDao gestorDao, CidadaoDao cidadaoDao, BCryptPasswordEncoder encoder) {
        this.gestorDao = gestorDao;
        this.cidadaoDao = cidadaoDao;
        this.encoder = encoder;
    }

    public LoginResponseDto login(String email, String senha) throws SQLException {
        LoginResponseDto resposta = new LoginResponseDto();

        Gestor gestor = gestorDao.buscarPorEmail(email.trim());
        if (gestor != null) {
            if (!encoder.matches(senha, gestor.getSenha())) throw new CredenciaisInvalidasException(MENSAGEM_401);
            resposta.setId(gestor.getId());
            resposta.setNome(gestor.getNome());
            resposta.setPerfil(Perfil.GESTOR.name());
            return resposta;
        }

        Cidadao cidadao = cidadaoDao.buscarPorEmail(email.trim());
        if (cidadao == null || !encoder.matches(senha, cidadao.getSenha())) {
            throw new CredenciaisInvalidasException(MENSAGEM_401);
        }
        resposta.setId(cidadao.getId());
        resposta.setNome(cidadao.getNome());
        resposta.setPerfil(Perfil.CIDADAO.name());
        return resposta;
    }
}
