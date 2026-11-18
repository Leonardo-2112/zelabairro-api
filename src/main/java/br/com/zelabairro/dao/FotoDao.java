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
import br.com.zelabairro.model.Foto;

//tb_foto: a imagem fica no BLOB bl_imagem (setBytes para gravar, getBytes para ler)
@Repository
public class FotoDao {

    private static final String INSERT_SQL = "INSERT INTO tb_foto (cd_foto, cd_ocorrencia, bl_imagem, ds_tipo, nr_tamanho) "
            + "VALUES (sq_tb_foto.NEXTVAL, ?, ?, ?, ?)";
    //Lista sem os bytes (mais leve)
    private static final String SELECT_SEM_IMAGEM_SQL = "SELECT cd_foto, cd_ocorrencia, ds_tipo, nr_tamanho, dt_envio FROM tb_foto";

    private final DataSource dataSource;

    public FotoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Foto foto) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_foto"})) {
            stmt.setInt(1, foto.getIdOcorrencia());
            stmt.setBytes(2, foto.getImagem());
            stmt.setString(3, foto.getTipo());
            stmt.setLong(4, foto.getTamanho());
            stmt.executeUpdate();
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) foto.setId(chave.getInt(1));
            }
        }
    }

    public List<Foto> listarPorOcorrencia(int idOcorrencia) throws SQLException {
        List<Foto> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SEM_IMAGEM_SQL + " WHERE cd_ocorrencia = ? ORDER BY cd_foto")) {
            stmt.setInt(1, idOcorrencia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(getFoto(rs));
                }
            }
        }
        return lista;
    }

    //Busca sem os bytes (para conferir a qual ocorrencia a foto pertence)
    public Foto buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SEM_IMAGEM_SQL + " WHERE cd_foto = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Foto " + id + " não encontrada.");
                }
                return getFoto(rs);
            }
        }
    }

    //Busca com os bytes, para GET /fotos/{id} devolver a imagem
    public Foto buscarComImagem(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(
                     "SELECT cd_foto, cd_ocorrencia, ds_tipo, nr_tamanho, dt_envio, bl_imagem FROM tb_foto WHERE cd_foto = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Foto " + id + " não encontrada.");
                }
                Foto foto = getFoto(rs);
                foto.setImagem(rs.getBytes("bl_imagem"));
                return foto;
            }
        }
    }

    //RN18: no maximo 3 fotos por ocorrencia (o CHECK do banco nao consegue contar)
    public int contarPorOcorrencia(int idOcorrencia) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("SELECT COUNT(*) FROM tb_foto WHERE cd_ocorrencia = ?")) {
            stmt.setInt(1, idOcorrencia);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement("DELETE FROM tb_foto WHERE cd_foto = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Foto " + id + " não encontrada.");
            }
        }
    }

    //Usado ao excluir a ocorrencia: apaga as fotos dela na mesma transacao
    public void removerPorOcorrencia(Connection conexao, int idOcorrencia) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement("DELETE FROM tb_foto WHERE cd_ocorrencia = ?")) {
            stmt.setInt(1, idOcorrencia);
            stmt.executeUpdate();
        }
    }

    private static Foto getFoto(ResultSet rs) throws SQLException {
        Foto foto = new Foto();
        foto.setId(rs.getInt("cd_foto"));
        foto.setIdOcorrencia(rs.getInt("cd_ocorrencia"));
        foto.setTipo(rs.getString("ds_tipo"));
        foto.setTamanho(rs.getLong("nr_tamanho"));
        foto.setDataEnvio(rs.getDate("dt_envio").toLocalDate());
        return foto;
    }
}
