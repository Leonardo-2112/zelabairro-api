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
import br.com.zelabairro.model.Categoria;
import br.com.zelabairro.model.Orgao;

//tb_categoria: CRUD com JDBC puro (JOIN com tb_orgao para trazer o nome do orgao)
@Repository
public class CategoriaDao {

    private static final String SELECT_SQL = "SELECT c.cd_categoria, c.nm_categoria, c.ds_categoria, c.nr_prazo_dias, c.nr_gravidade, "
            + "o.cd_orgao, o.nm_orgao FROM tb_categoria c JOIN tb_orgao o ON o.cd_orgao = c.cd_orgao";
    private static final String INSERT_SQL = "INSERT INTO tb_categoria (cd_categoria, nm_categoria, ds_categoria, nr_prazo_dias, nr_gravidade, cd_orgao) "
            + "VALUES (sq_tb_categoria.NEXTVAL, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE tb_categoria SET nm_categoria = ?, ds_categoria = ?, nr_prazo_dias = ?, "
            + "nr_gravidade = ?, cd_orgao = ? WHERE cd_categoria = ?";
    private static final String DELETE_SQL = "DELETE FROM tb_categoria WHERE cd_categoria = ?";

    private final DataSource dataSource;

    public CategoriaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Categoria categoria) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_categoria"})) {
            preencher(stmt, categoria);
            stmt.executeUpdate();
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) categoria.setId(chave.getInt(1));
            }
        }
    }

    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " ORDER BY c.nm_categoria");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(getCategoria(rs));
            }
        }
        return lista;
    }

    public Categoria buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE c.cd_categoria = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Categoria " + id + " não encontrada.");
                }
                return getCategoria(rs);
            }
        }
    }

    public void atualizar(Categoria categoria) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            preencher(stmt, categoria);
            stmt.setInt(6, categoria.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Categoria " + categoria.getId() + " não encontrada.");
            }
        }
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Categoria " + id + " não encontrada.");
            }
        }
    }

    //Nome ja usado por outra categoria? (UNIQUE do banco)
    public boolean existeNome(String nome, int idIgnorar) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     "SELECT COUNT(*) FROM tb_categoria WHERE UPPER(nm_categoria) = UPPER(?) AND cd_categoria <> ?")) {
            stmt.setString(1, nome);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    //RN14: quantas ocorrencias estao ligadas a categoria
    public int contarOcorrencias(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("SELECT COUNT(*) FROM tb_ocorrencia WHERE cd_categoria = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static void preencher(PreparedStatement stmt, Categoria categoria) throws SQLException {
        stmt.setString(1, categoria.getNome());
        stmt.setString(2, categoria.getDescricao());
        stmt.setInt(3, categoria.getPrazoDias());
        stmt.setInt(4, categoria.getGravidade());
        stmt.setInt(5, categoria.getOrgao().getId());
    }

    private static Categoria getCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("cd_categoria"));
        categoria.setNome(rs.getString("nm_categoria"));
        categoria.setDescricao(rs.getString("ds_categoria"));
        categoria.setPrazoDias(rs.getInt("nr_prazo_dias"));
        categoria.setGravidade(rs.getInt("nr_gravidade"));
        Orgao orgao = new Orgao(rs.getInt("cd_orgao"));
        orgao.setNome(rs.getString("nm_orgao"));
        categoria.setOrgao(orgao);
        return categoria;
    }
}
