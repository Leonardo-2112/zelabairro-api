package br.com.zelabairro.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.BairroDao;
import br.com.zelabairro.dao.CategoriaDao;
import br.com.zelabairro.dao.CidadaoDao;
import br.com.zelabairro.dao.FotoDao;
import br.com.zelabairro.dao.OcorrenciaDao;
import br.com.zelabairro.exception.DadosInvalidosException;
import br.com.zelabairro.exception.RegraNegocioException;
import br.com.zelabairro.model.Ocorrencia;
import br.com.zelabairro.model.enums.StatusOcorrencia;

//Regras da ocorrencia: RN01 (FKs existem), RN04 (nasce ABERTA), RN09 (edicao/exclusao),
//RN10 (lista ordenada por prioridade) e RN12 (previsao da IA opcional)
@Service
public class OcorrenciaBo {

    //RN10: maior prioridade primeiro; fechadas (sem prioridade) no fim; empate: a mais antiga primeiro
    private static final Comparator<Ocorrencia> POR_PRIORIDADE = Comparator
            .comparing(Ocorrencia::getPontuacaoPrioridade, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Ocorrencia::getDataAbertura)
            .thenComparing(Ocorrencia::getId);

    private final OcorrenciaDao ocorrenciaDao;
    private final CidadaoDao cidadaoDao;
    private final CategoriaDao categoriaDao;
    private final BairroDao bairroDao;
    private final FotoDao fotoDao;
    private final PrevisaoIaBo previsaoIaBo;
    private final DataSource dataSource;

    public OcorrenciaBo(OcorrenciaDao ocorrenciaDao, CidadaoDao cidadaoDao, CategoriaDao categoriaDao, BairroDao bairroDao,
                        FotoDao fotoDao, PrevisaoIaBo previsaoIaBo, DataSource dataSource) {
        this.ocorrenciaDao = ocorrenciaDao;
        this.cidadaoDao = cidadaoDao;
        this.categoriaDao = categoriaDao;
        this.bairroDao = bairroDao;
        this.fotoDao = fotoDao;
        this.previsaoIaBo = previsaoIaBo;
        this.dataSource = dataSource;
    }

    //Filtros opcionais; status chega como texto da query string
    public List<Ocorrencia> listar(String status, Integer idBairro, Integer idCategoria) throws SQLException {
        StatusOcorrencia filtroStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                filtroStatus = StatusOcorrencia.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new DadosInvalidosException("RN04: status deve ser ABERTA, EM_ANDAMENTO, RESOLVIDA ou CANCELADA.");
            }
        }
        List<Ocorrencia> lista = ocorrenciaDao.listar(filtroStatus, idBairro, idCategoria);
        return lista.stream().sorted(POR_PRIORIDADE).toList();
    }

    public List<Ocorrencia> listarPorBairro(int idBairro) throws SQLException {
        bairroDao.buscarPorId(idBairro); //404 se o bairro nao existir
        return listar(null, idBairro, null);
    }

    public List<Ocorrencia> listarPorCategoria(int idCategoria) throws SQLException {
        categoriaDao.buscarPorId(idCategoria);
        return listar(null, null, idCategoria);
    }

    public Ocorrencia buscar(int id) throws SQLException {
        return ocorrenciaDao.buscarPorId(id);
    }

    public Ocorrencia cadastrar(Ocorrencia ocorrencia) throws SQLException {
        //RN01: autor, categoria e bairro precisam existir (senao 404)
        cidadaoDao.buscarPorId(ocorrencia.getCidadao().getId());
        ocorrencia.setCategoria(categoriaDao.buscarPorId(ocorrencia.getCategoria().getId()));
        ocorrencia.setBairro(bairroDao.buscarPorId(ocorrencia.getBairro().getId()));

        //RN04: nasce ABERTA, com abertura = hoje
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        ocorrencia.setDataAbertura(LocalDate.now());

        //Secao 7.4: a IA e chamada uma unica vez, aqui (RN12 e D3 tratadas dentro)
        previsaoIaBo.preencherPrevisao(ocorrencia);

        ocorrenciaDao.cadastrar(ocorrencia);
        return ocorrenciaDao.buscarPorId(ocorrencia.getId());
    }

    //RN09: so edita enquanto ABERTA (descricao, endereco, categoria e bairro)
    public Ocorrencia atualizar(Ocorrencia ocorrencia) throws SQLException {
        Ocorrencia atual = ocorrenciaDao.buscarPorId(ocorrencia.getId());
        if (atual.getStatus() != StatusOcorrencia.ABERTA) {
            throw new RegraNegocioException("RN09: a ocorrência só pode ser editada enquanto estiver ABERTA (status atual: "
                    + atual.getStatus() + ").");
        }
        categoriaDao.buscarPorId(ocorrencia.getCategoria().getId());
        bairroDao.buscarPorId(ocorrencia.getBairro().getId());
        ocorrenciaDao.atualizar(ocorrencia);
        return ocorrenciaDao.buscarPorId(ocorrencia.getId());
    }

    //RN09: com apoios ou atualizacoes nao exclui (deve ser CANCELADA). As fotos saem junto, numa transacao
    public void remover(int id) throws SQLException {
        ocorrenciaDao.buscarPorId(id);
        if (ocorrenciaDao.contarVinculos(id) > 0) {
            throw new RegraNegocioException("RN09: a ocorrência tem apoios ou atualizações e não pode ser excluída; ela deve ser CANCELADA.");
        }
        try (Connection conexao = dataSource.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                fotoDao.removerPorOcorrencia(conexao, id);
                ocorrenciaDao.remover(conexao, id);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        }
    }
}
