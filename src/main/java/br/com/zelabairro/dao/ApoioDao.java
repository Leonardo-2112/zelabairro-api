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
import br.com.zelabairro.model.Apoio;
import br.com.zelabairro.model.Cidadao;

//tb_apoio: associativa do N:N cidadao <-> ocorrencia (chave composta, sem sequence)
@Repository
public class ApoioDao {

    private static final String SELECT_SQL = "SELECT a.cd_ocorrencia, a.dt_apoio, c.cd_cidadao, c.nm_cidadao "
            + "FROM tb_apoio a JOIN tb_cidadao c ON c.cd_cidadao = a.cd_cidadao";
    private static final String INSERT_SQL = "INSERT INTO tb_apoio (cd_cidadao, cd_ocorrencia) VALUES (?, ?)";
    private static final String DELETE_SQL = "DELETE FROM tb_apoio WHERE cd_ocorrencia = ? AND cd_cidadao = ?";

    private final DataSource dataSource;

    public ApoioDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Apoio apoio) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, apoio.getCidadao().getId());
            stmt.setInt(2, apoio.getIdOcorrencia());
            stmt.executeUpdate();
        }
    }

    public List<Apoio> listarPorOcorrencia(int idOcorrencia) throws SQLException {
        List<Apoio> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE a.cd_ocorrencia = ? ORDER BY a.dt_apoio")) {
            stmt.setInt(1, idOcorrencia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(getApoio(rs));
                }
            }
        }
        return lista;
    }

    public Apoio buscar(int idOcorrencia, int idCidadao) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE a.cd_ocorrencia = ? AND a.cd_cidadao = ?")) {
            stmt.setInt(1, idOcorrencia);
            stmt.setInt(2, idCidadao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("O cidadão " + idCidadao + " não apoia a ocorrência " + idOcorrencia + ".");
                }
                return getApoio(rs);
            }
        }
    }

    //RN08: o cidadao ja apoia esta ocorrencia?
    public boolean existe(int idOcorrencia, int idCidadao) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     "SELECT COUNT(*) FROM tb_apoio WHERE cd_ocorrencia = ? AND cd_cidadao = ?")) {
            stmt.setInt(1, idOcorrencia);
            stmt.setInt(2, idCidadao);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void remover(int idOcorrencia, int idCidadao) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, idOcorrencia);
            stmt.setInt(2, idCidadao);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("O cidadão " + idCidadao + " não apoia a ocorrência " + idOcorrencia + ".");
            }
        }
    }

    private static Apoio getApoio(ResultSet rs) throws SQLException {
        Apoio apoio = new Apoio();
        apoio.setIdOcorrencia(rs.getInt("cd_ocorrencia"));
        apoio.setData(rs.getDate("dt_apoio").toLocalDate());
        Cidadao cidadao = new Cidadao(rs.getInt("cd_cidadao"));
        cidadao.setNome(rs.getString("nm_cidadao"));
        apoio.setCidadao(cidadao);
        return apoio;
    }
}
