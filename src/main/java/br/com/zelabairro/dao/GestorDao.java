package br.com.zelabairro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.zelabairro.exception.EntidadeNaoEncontradaException;
import br.com.zelabairro.model.Gestor;
import br.com.zelabairro.model.Orgao;

//tb_gestor: CRUD com JDBC puro (JOIN com tb_orgao para trazer o nome do orgao)
@Repository
public class GestorDao {

    private static final String SELECT_SQL = "SELECT g.cd_gestor, g.nm_gestor, g.ds_email, g.ds_senha, g.cd_gestor_cadastro, "
            + "g.dt_cadastro, o.cd_orgao, o.nm_orgao FROM tb_gestor g JOIN tb_orgao o ON o.cd_orgao = g.cd_orgao";
    private static final String INSERT_SQL = "INSERT INTO tb_gestor (cd_gestor, nm_gestor, ds_email, ds_senha, cd_orgao, cd_gestor_cadastro) "
            + "VALUES (sq_tb_gestor.NEXTVAL, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE tb_gestor SET nm_gestor = ?, ds_email = ?, ds_senha = ?, cd_orgao = ? WHERE cd_gestor = ?";
    private static final String DELETE_SQL = "DELETE FROM tb_gestor WHERE cd_gestor = ?";

    private final DataSource dataSource;

    public GestorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Gestor gestor) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_gestor"})) {
            stmt.setString(1, gestor.getNome());
            stmt.setString(2, gestor.getEmail());
            stmt.setString(3, gestor.getSenha());
            stmt.setInt(4, gestor.getOrgao().getId());
            if (gestor.getIdGestorCadastro() != null) {
                stmt.setInt(5, gestor.getIdGestorCadastro()); //RN17: quem cadastrou
            } else {
                stmt.setNull(5, Types.NUMERIC);
            }
            stmt.executeUpdate();
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) gestor.setId(chave.getInt(1));
            }
        }
    }

    public List<Gestor> listar() throws SQLException {
        List<Gestor> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " ORDER BY g.nm_gestor");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(getGestor(rs));
            }
        }
        return lista;
    }

    public Gestor buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE g.cd_gestor = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Gestor " + id + " não encontrado.");
                }
                return getGestor(rs);
            }
        }
    }

    //Login (RN15): devolve null se o e-mail nao existir
    public Gestor buscarPorEmail(String email) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE LOWER(g.ds_email) = LOWER(?)")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? getGestor(rs) : null;
            }
        }
    }

    //RN16: o id enviado no cabecalho X-Gestor-Id existe em tb_gestor?
    public boolean existe(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("SELECT COUNT(*) FROM tb_gestor WHERE cd_gestor = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void atualizar(Gestor gestor) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, gestor.getNome());
            stmt.setString(2, gestor.getEmail());
            stmt.setString(3, gestor.getSenha());
            stmt.setInt(4, gestor.getOrgao().getId());
            stmt.setInt(5, gestor.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Gestor " + gestor.getId() + " não encontrado.");
            }
        }
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Gestor " + id + " não encontrado.");
            }
        }
    }

    //RN02: e-mail ja usado por outro gestor?
    public boolean existeEmail(String email, int idIgnorar) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     "SELECT COUNT(*) FROM tb_gestor WHERE LOWER(ds_email) = LOWER(?) AND cd_gestor <> ?")) {
            stmt.setString(1, email);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    //Atualizacoes feitas + gestores cadastrados por ele: se tiver algum, nao pode ser removido (409)
    public int contarVinculos(int id) throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM tb_atualizacao WHERE cd_gestor = ?) "
                + "+ (SELECT COUNT(*) FROM tb_gestor WHERE cd_gestor_cadastro = ?) FROM dual";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static Gestor getGestor(ResultSet rs) throws SQLException {
        Gestor gestor = new Gestor(rs.getInt("cd_gestor"));
        gestor.setNome(rs.getString("nm_gestor"));
        gestor.setEmail(rs.getString("ds_email"));
        gestor.setSenha(rs.getString("ds_senha"));
        int cadastradoPor = rs.getInt("cd_gestor_cadastro");
        gestor.setIdGestorCadastro(rs.wasNull() ? null : cadastradoPor);
        gestor.setDataCadastro(rs.getDate("dt_cadastro").toLocalDate());
        Orgao orgao = new Orgao(rs.getInt("cd_orgao"));
        orgao.setNome(rs.getString("nm_orgao"));
        gestor.setOrgao(orgao);
        return gestor;
    }
}
