package br.com.zelabairro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.zelabairro.exception.EntidadeNaoEncontradaException;
import br.com.zelabairro.model.Cidadao;

//tb_cidadao: CRUD com JDBC puro (ds_senha guarda so o hash BCrypt)
@Repository
public class CidadaoDao {

    private static final String SELECT_SQL = "SELECT cd_cidadao, nm_cidadao, ds_email, ds_senha, nr_telefone, dt_cadastro FROM tb_cidadao";
    private static final String INSERT_SQL = "INSERT INTO tb_cidadao (cd_cidadao, nm_cidadao, ds_email, ds_senha, nr_telefone) "
            + "VALUES (sq_tb_cidadao.NEXTVAL, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE tb_cidadao SET nm_cidadao = ?, ds_email = ?, ds_senha = ?, nr_telefone = ? WHERE cd_cidadao = ?";
    private static final String DELETE_SQL = "DELETE FROM tb_cidadao WHERE cd_cidadao = ?";

    private final DataSource dataSource;

    public CidadaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Cidadao cidadao) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_cidadao"})) {
            stmt.setString(1, cidadao.getNome());
            stmt.setString(2, cidadao.getEmail());
            stmt.setString(3, cidadao.getSenha());
            stmt.setString(4, cidadao.getTelefone());
            stmt.executeUpdate();
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) cidadao.setId(chave.getInt(1));
            }
        }
    }

    public List<Cidadao> listar() throws SQLException {
        List<Cidadao> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " ORDER BY nm_cidadao");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(getCidadao(rs));
            }
        }
        return lista;
    }

    public Cidadao buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE cd_cidadao = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Cidadão " + id + " não encontrado.");
                }
                return getCidadao(rs);
            }
        }
    }

    //Login (RN15): devolve null se o e-mail nao existir
    public Cidadao buscarPorEmail(String email) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE LOWER(ds_email) = LOWER(?)")) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? getCidadao(rs) : null;
            }
        }
    }

    public void atualizar(Cidadao cidadao) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, cidadao.getNome());
            stmt.setString(2, cidadao.getEmail());
            stmt.setString(3, cidadao.getSenha());
            stmt.setString(4, cidadao.getTelefone());
            stmt.setInt(5, cidadao.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Cidadão " + cidadao.getId() + " não encontrado.");
            }
        }
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Cidadão " + id + " não encontrado.");
            }
        }
    }

    //RN02: e-mail ja usado por outro cidadao? (idIgnorar = o proprio, no PUT)
    public boolean existeEmail(String email, int idIgnorar) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     "SELECT COUNT(*) FROM tb_cidadao WHERE LOWER(ds_email) = LOWER(?) AND cd_cidadao <> ?")) {
            stmt.setString(1, email);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    //Ocorrencias criadas + apoios dados: se tiver algum, nao pode ser removido (409)
    public int contarVinculos(int id) throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM tb_ocorrencia WHERE cd_cidadao = ?) "
                + "+ (SELECT COUNT(*) FROM tb_apoio WHERE cd_cidadao = ?) FROM dual";
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

    private static Cidadao getCidadao(ResultSet rs) throws SQLException {
        Cidadao cidadao = new Cidadao(rs.getInt("cd_cidadao"));
        cidadao.setNome(rs.getString("nm_cidadao"));
        cidadao.setEmail(rs.getString("ds_email"));
        cidadao.setSenha(rs.getString("ds_senha"));
        cidadao.setTelefone(rs.getString("nr_telefone"));
        cidadao.setDataCadastro(rs.getDate("dt_cadastro").toLocalDate());
        return cidadao;
    }
}
