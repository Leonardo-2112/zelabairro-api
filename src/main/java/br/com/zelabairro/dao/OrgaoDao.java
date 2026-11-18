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
import br.com.zelabairro.model.Orgao;

//tb_orgao: so leitura (os orgaos vem do seed; aparecem na categoria e no gestor)
@Repository
public class OrgaoDao {

    private static final String SELECT_SQL = "SELECT cd_orgao, nm_orgao, ds_email, nr_telefone FROM tb_orgao";

    private final DataSource dataSource;

    public OrgaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Orgao> listar() throws SQLException {
        List<Orgao> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " ORDER BY nm_orgao");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(getOrgao(rs));
            }
        }
        return lista;
    }

    public Orgao buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE cd_orgao = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Órgão " + id + " não encontrado.");
                }
                return getOrgao(rs);
            }
        }
    }

    private static Orgao getOrgao(ResultSet rs) throws SQLException {
        Orgao orgao = new Orgao(rs.getInt("cd_orgao"));
        orgao.setNome(rs.getString("nm_orgao"));
        orgao.setEmail(rs.getString("ds_email"));
        orgao.setTelefone(rs.getString("nr_telefone"));
        return orgao;
    }
}
