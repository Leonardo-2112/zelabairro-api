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
import br.com.zelabairro.model.Atualizacao;
import br.com.zelabairro.model.Gestor;
import br.com.zelabairro.model.enums.StatusOcorrencia;

//tb_atualizacao: historico de status (lado N do 1:N com tb_ocorrencia)
@Repository
public class AtualizacaoDao {

    private static final String SELECT_SQL = "SELECT a.cd_atualizacao, a.cd_ocorrencia, a.st_anterior, a.st_novo, a.ds_comentario, "
            + "a.dt_atualizacao, g.cd_gestor, g.nm_gestor FROM tb_atualizacao a JOIN tb_gestor g ON g.cd_gestor = a.cd_gestor";
    private static final String INSERT_SQL = "INSERT INTO tb_atualizacao (cd_atualizacao, cd_ocorrencia, st_anterior, st_novo, "
            + "ds_comentario, cd_gestor) VALUES (sq_tb_atualizacao.NEXTVAL, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE tb_atualizacao SET ds_comentario = ? WHERE cd_atualizacao = ? AND cd_ocorrencia = ?";

    private final DataSource dataSource;

    public AtualizacaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //Recebe a Connection do BO: o INSERT e o UPDATE do status ficam na mesma transacao (RN06)
    public void cadastrar(Connection conexao, Atualizacao atualizacao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_atualizacao"})) {
            stmt.setInt(1, atualizacao.getIdOcorrencia());
            stmt.setString(2, atualizacao.getStatusAnterior().name());
            stmt.setString(3, atualizacao.getStatusNovo().name());
            stmt.setString(4, atualizacao.getComentario());
            stmt.setInt(5, atualizacao.getGestor().getId());
            stmt.executeUpdate();
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) atualizacao.setId(chave.getInt(1));
            }
        }
    }

    //Historico de uma ocorrencia, do mais antigo para o mais recente
    public List<Atualizacao> listarPorOcorrencia(int idOcorrencia) throws SQLException {
        List<Atualizacao> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE a.cd_ocorrencia = ? ORDER BY a.dt_atualizacao, a.cd_atualizacao")) {
            stmt.setInt(1, idOcorrencia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(getAtualizacao(rs));
                }
            }
        }
        return lista;
    }

    public Atualizacao buscarPorId(int idOcorrencia, int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE a.cd_ocorrencia = ? AND a.cd_atualizacao = ?")) {
            stmt.setInt(1, idOcorrencia);
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Atualização " + id + " não encontrada nesta ocorrência.");
                }
                return getAtualizacao(rs);
            }
        }
    }

    //So o comentario pode ser corrigido: o status do historico nao muda (RN06)
    public void atualizarComentario(int idOcorrencia, int id, String comentario) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, comentario);
            stmt.setInt(2, id);
            stmt.setInt(3, idOcorrencia);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Atualização " + id + " não encontrada nesta ocorrência.");
            }
        }
    }

    private static Atualizacao getAtualizacao(ResultSet rs) throws SQLException {
        Atualizacao atualizacao = new Atualizacao();
        atualizacao.setId(rs.getInt("cd_atualizacao"));
        atualizacao.setIdOcorrencia(rs.getInt("cd_ocorrencia"));
        atualizacao.setStatusAnterior(StatusOcorrencia.valueOf(rs.getString("st_anterior")));
        atualizacao.setStatusNovo(StatusOcorrencia.valueOf(rs.getString("st_novo")));
        atualizacao.setComentario(rs.getString("ds_comentario"));
        atualizacao.setData(rs.getDate("dt_atualizacao").toLocalDate());
        Gestor gestor = new Gestor(rs.getInt("cd_gestor"));
        gestor.setNome(rs.getString("nm_gestor"));
        atualizacao.setGestor(gestor);
        return atualizacao;
    }
}
