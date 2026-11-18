package br.com.zelabairro.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.zelabairro.exception.EntidadeNaoEncontradaException;
import br.com.zelabairro.model.Bairro;
import br.com.zelabairro.model.Categoria;
import br.com.zelabairro.model.Cidadao;
import br.com.zelabairro.model.Ocorrencia;
import br.com.zelabairro.model.Orgao;
import br.com.zelabairro.model.enums.StatusOcorrencia;

//tb_ocorrencia: CRUD com JDBC puro. A consulta junta autor, categoria, orgao e bairro
//e conta os apoios e as fotos, para a saida e para o calculo da prioridade (RN10)
@Repository
public class OcorrenciaDao {

    private static final String SELECT_SQL = "SELECT o.cd_ocorrencia, o.ds_ocorrencia, o.ds_endereco, o.st_ocorrencia, "
            + "o.dt_abertura, o.dt_resolucao, o.nr_prazo_previsto, o.fl_risco_atraso, "
            + "ci.cd_cidadao, ci.nm_cidadao, "
            + "c.cd_categoria, c.nm_categoria, c.ds_categoria, c.nr_prazo_dias, c.nr_gravidade, org.cd_orgao, org.nm_orgao, "
            + "b.cd_bairro, b.nm_bairro, b.nm_subprefeitura, b.nr_populacao, "
            + "(SELECT COUNT(*) FROM tb_apoio a WHERE a.cd_ocorrencia = o.cd_ocorrencia) AS qtd_apoios, "
            + "(SELECT COUNT(*) FROM tb_foto f WHERE f.cd_ocorrencia = o.cd_ocorrencia) AS qtd_fotos "
            + "FROM tb_ocorrencia o "
            + "JOIN tb_cidadao ci ON ci.cd_cidadao = o.cd_cidadao "
            + "JOIN tb_categoria c ON c.cd_categoria = o.cd_categoria "
            + "JOIN tb_orgao org ON org.cd_orgao = c.cd_orgao "
            + "JOIN tb_bairro b ON b.cd_bairro = o.cd_bairro";

    //RN04: nasce ABERTA, com abertura = hoje e sem resolucao
    private static final String INSERT_SQL = "INSERT INTO tb_ocorrencia (cd_ocorrencia, ds_ocorrencia, ds_endereco, st_ocorrencia, "
            + "dt_abertura, nr_prazo_previsto, fl_risco_atraso, cd_cidadao, cd_categoria, cd_bairro) "
            + "VALUES (sq_tb_ocorrencia.NEXTVAL, ?, ?, 'ABERTA', ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE tb_ocorrencia SET ds_ocorrencia = ?, ds_endereco = ?, cd_categoria = ?, cd_bairro = ? "
            + "WHERE cd_ocorrencia = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE tb_ocorrencia SET st_ocorrencia = ?, dt_resolucao = ? WHERE cd_ocorrencia = ?";
    private static final String DELETE_SQL = "DELETE FROM tb_ocorrencia WHERE cd_ocorrencia = ?";

    private final DataSource dataSource;

    public OcorrenciaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Ocorrencia ocorrencia) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[] {"cd_ocorrencia"})) {
            stmt.setString(1, ocorrencia.getDescricao());
            stmt.setString(2, ocorrencia.getEndereco());
            stmt.setDate(3, Date.valueOf(ocorrencia.getDataAbertura()));
            //Previsao da IA: nula se a IA estiver fora do ar (RN12)
            if (ocorrencia.getPrazoPrevisto() != null) {
                stmt.setInt(4, ocorrencia.getPrazoPrevisto());
            } else {
                stmt.setNull(4, Types.NUMERIC);
            }
            stmt.setString(5, ocorrencia.getRiscoAtraso());
            stmt.setInt(6, ocorrencia.getCidadao().getId());
            stmt.setInt(7, ocorrencia.getCategoria().getId());
            stmt.setInt(8, ocorrencia.getBairro().getId());
            stmt.executeUpdate();
            try (ResultSet chave = stmt.getGeneratedKeys()) {
                if (chave.next()) ocorrencia.setId(chave.getInt(1));
            }
        }
    }

    //Filtros opcionais (null = sem filtro): ?status=&idBairro=&idCategoria=
    public List<Ocorrencia> listar(StatusOcorrencia status, Integer idBairro, Integer idCategoria) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_SQL + " WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (status != null) {
            sql.append(" AND o.st_ocorrencia = ?");
            parametros.add(status.name());
        }
        if (idBairro != null) {
            sql.append(" AND o.cd_bairro = ?");
            parametros.add(idBairro);
        }
        if (idCategoria != null) {
            sql.append(" AND o.cd_categoria = ?");
            parametros.add(idCategoria);
        }
        sql.append(" ORDER BY o.dt_abertura, o.cd_ocorrencia");

        List<Ocorrencia> lista = new ArrayList<>();
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(getOcorrencia(rs));
                }
            }
        }
        return lista;
    }

    public Ocorrencia buscarPorId(int id) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL + " WHERE o.cd_ocorrencia = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Ocorrência " + id + " não encontrada.");
                }
                return getOcorrencia(rs);
            }
        }
    }

    //Edicao pelo cidadao (RN09: so enquanto ABERTA, conferido no BO)
    public void atualizar(Ocorrencia ocorrencia) throws SQLException {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, ocorrencia.getDescricao());
            stmt.setString(2, ocorrencia.getEndereco());
            stmt.setInt(3, ocorrencia.getCategoria().getId());
            stmt.setInt(4, ocorrencia.getBairro().getId());
            stmt.setInt(5, ocorrencia.getId());
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Ocorrência " + ocorrencia.getId() + " não encontrada.");
            }
        }
    }

    //Le o status atual travando a linha (SELECT ... FOR UPDATE) ate o commit:
    //duas atualizacoes ao mesmo tempo nao conseguem fazer a mesma transicao (RN05)
    public StatusOcorrencia buscarStatusParaAtualizar(Connection conexao, int id) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(
                "SELECT st_ocorrencia FROM tb_ocorrencia WHERE cd_ocorrencia = ? FOR UPDATE")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Ocorrência " + id + " não encontrada.");
                }
                return StatusOcorrencia.valueOf(rs.getString("st_ocorrencia"));
            }
        }
    }

    //RN06: muda o status junto com o INSERT da atualizacao (mesma Connection = mesma transacao)
    public void atualizarStatus(Connection conexao, int id, StatusOcorrencia status, LocalDate dataResolucao) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(UPDATE_STATUS_SQL)) {
            stmt.setString(1, status.name());
            if (dataResolucao != null) {
                stmt.setDate(2, Date.valueOf(dataResolucao));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    //Remocao dentro de uma transacao (as fotos sao apagadas antes, na mesma Connection)
    public void remover(Connection conexao, int id) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EntidadeNaoEncontradaException("Ocorrência " + id + " não encontrada.");
            }
        }
    }

    //RN09: apoios + atualizacoes. Se tiver algum, nao pode ser excluida (deve ser CANCELADA)
    public int contarVinculos(int id) throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM tb_apoio WHERE cd_ocorrencia = ?) "
                + "+ (SELECT COUNT(*) FROM tb_atualizacao WHERE cd_ocorrencia = ?) FROM dual";
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

    private static Ocorrencia getOcorrencia(ResultSet rs) throws SQLException {
        Ocorrencia ocorrencia = new Ocorrencia(rs.getInt("cd_ocorrencia"));
        ocorrencia.setDescricao(rs.getString("ds_ocorrencia"));
        ocorrencia.setEndereco(rs.getString("ds_endereco"));
        ocorrencia.setStatus(StatusOcorrencia.valueOf(rs.getString("st_ocorrencia")));
        ocorrencia.setDataAbertura(rs.getDate("dt_abertura").toLocalDate());
        Date resolucao = rs.getDate("dt_resolucao");
        ocorrencia.setDataResolucao(resolucao != null ? resolucao.toLocalDate() : null);
        int prazo = rs.getInt("nr_prazo_previsto");
        ocorrencia.setPrazoPrevisto(rs.wasNull() ? null : prazo);
        ocorrencia.setRiscoAtraso(rs.getString("fl_risco_atraso"));

        Cidadao autor = new Cidadao(rs.getInt("cd_cidadao"));
        autor.setNome(rs.getString("nm_cidadao"));
        ocorrencia.setCidadao(autor);

        Categoria categoria = new Categoria(rs.getInt("cd_categoria"));
        categoria.setNome(rs.getString("nm_categoria"));
        categoria.setDescricao(rs.getString("ds_categoria"));
        categoria.setPrazoDias(rs.getInt("nr_prazo_dias"));
        categoria.setGravidade(rs.getInt("nr_gravidade"));
        Orgao orgao = new Orgao(rs.getInt("cd_orgao"));
        orgao.setNome(rs.getString("nm_orgao"));
        categoria.setOrgao(orgao);
        ocorrencia.setCategoria(categoria);

        Bairro bairro = new Bairro(rs.getInt("cd_bairro"));
        bairro.setNome(rs.getString("nm_bairro"));
        bairro.setSubprefeitura(rs.getString("nm_subprefeitura"));
        int populacao = rs.getInt("nr_populacao");
        bairro.setPopulacao(rs.wasNull() ? null : populacao);
        ocorrencia.setBairro(bairro);

        ocorrencia.setQtdApoios(rs.getInt("qtd_apoios"));
        ocorrencia.setQtdFotos(rs.getInt("qtd_fotos"));
        return ocorrencia;
    }
}
