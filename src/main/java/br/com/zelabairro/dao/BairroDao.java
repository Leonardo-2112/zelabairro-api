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
import br.com.zelabairro.model.Bairro;

//tb_bairro: CRUD com JDBC puro
@Repository
public class BairroDao {

    private static final String SELECT_SQL = "SELECT cd_bairro, nm_bairro, nm_subprefeitura, nr_populacao FROM tb_bairro";
    private static final String INSERT_SQL = "INSERT INTO tb_bairro (cd_bairro, nm_bairro, nm_subprefeitura, nr_populacao) "
            + "VALUES (sq_tb_bairro.NEXTVAL, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE tb_bairro SET nm_bairro = ?, nm_subprefeitura = ?, nr_populacao = ? WHERE cd_bairro = ?";
    private static final String DELETE_SQL = "DELETE FROM tb_bairro WHERE cd_bairro = ?";

    private final DataSource dataSource;

    //DataSource recebido pelo construtor (o Spring entrega o pool HikariCP)
    public BairroDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Bairro bairro) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_bairro"})) {
            preencher(stmt, bairro);
            stmt.executeUpdate();
            //Le o codigo gerado pela sequence
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) bairro.setId(chave.getInt(1));
            }
        }
    }

    public List<Bairro> listar() throws SQLException {
        List<Bairro> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " ORDER BY nm_bairro");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(getBairro(rs));
            }
        }
        return lista;
    }

    public Bairro buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE cd_bairro = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Bairro " + id + " não encontrado.");
                }
                return getBairro(rs);
            }
        }
    }

    public void atualizar(Bairro bairro) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            preencher(stmt, bairro);
            stmt.setInt(4, bairro.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Bairro " + bairro.getId() + " não encontrado.");
            }
        }
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Bairro " + id + " não encontrado.");
            }
        }
    }

    //Mesmo nome + subprefeitura ja cadastrado? (UNIQUE do banco). idIgnorar = o proprio, no PUT
    public boolean existe(String nome, String subprefeitura, int idIgnorar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_bairro WHERE UPPER(nm_bairro) = UPPER(?) "
                + "AND UPPER(nm_subprefeitura) = UPPER(?) AND cd_bairro <> ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, subprefeitura);
            stmt.setInt(3, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    //RN14: quantas ocorrencias estao ligadas ao bairro
    public int contarOcorrencias(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("SELECT COUNT(*) FROM tb_ocorrencia WHERE cd_bairro = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static void preencher(PreparedStatement stmt, Bairro bairro) throws SQLException {
        stmt.setString(1, bairro.getNome());
        stmt.setString(2, bairro.getSubprefeitura());
        if (bairro.getPopulacao() != null) {
            stmt.setInt(3, bairro.getPopulacao());
        } else {
            stmt.setNull(3, Types.NUMERIC);
        }
    }

    private static Bairro getBairro(ResultSet rs) throws SQLException {
        Bairro bairro = new Bairro(rs.getInt("cd_bairro"));
        bairro.setNome(rs.getString("nm_bairro"));
        bairro.setSubprefeitura(rs.getString("nm_subprefeitura"));
        int populacao = rs.getInt("nr_populacao");
        bairro.setPopulacao(rs.wasNull() ? null : populacao);
        return bairro;
    }
}
