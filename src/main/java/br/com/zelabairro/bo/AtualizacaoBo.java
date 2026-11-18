package br.com.zelabairro.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import br.com.zelabairro.dao.AtualizacaoDao;
import br.com.zelabairro.dao.OcorrenciaDao;
import br.com.zelabairro.exception.RegraNegocioException;
import br.com.zelabairro.model.Atualizacao;
import br.com.zelabairro.model.Gestor;
import br.com.zelabairro.model.enums.StatusOcorrencia;

//Historico de status: RN05 (transicoes), RN06 (status so muda criando atualizacao) e RN16 (so gestor)
@Service
public class AtualizacaoBo {

    private final AtualizacaoDao atualizacaoDao;
    private final OcorrenciaDao ocorrenciaDao;
    private final AcessoBo acessoBo;
    private final DataSource dataSource;

    public AtualizacaoBo(AtualizacaoDao atualizacaoDao, OcorrenciaDao ocorrenciaDao, AcessoBo acessoBo, DataSource dataSource) {
        this.atualizacaoDao = atualizacaoDao;
        this.ocorrenciaDao = ocorrenciaDao;
        this.acessoBo = acessoBo;
        this.dataSource = dataSource;
    }

    public List<Atualizacao> listar(int idOcorrencia) throws SQLException {
        ocorrenciaDao.buscarPorId(idOcorrencia); //404 se a ocorrencia nao existir
        return atualizacaoDao.listarPorOcorrencia(idOcorrencia);
    }

    public Atualizacao buscar(int idOcorrencia, int id) throws SQLException {
        ocorrenciaDao.buscarPorId(idOcorrencia);
        return atualizacaoDao.buscarPorId(idOcorrencia, id);
    }

    public Atualizacao cadastrar(Integer idGestorLogado, int idOcorrencia, StatusOcorrencia novoStatus, String comentario)
            throws SQLException {
        acessoBo.validarGestor(idGestorLogado); //RN16: 403 se nao for gestor
        ocorrenciaDao.buscarPorId(idOcorrencia); //404

        Atualizacao atualizacao = new Atualizacao();
        try (Connection conexao = dataSource.getConnection()) {
            conexao.setAutoCommit(false); //INSERT + UPDATE juntos: ou grava os dois ou nenhum
            try {
                StatusOcorrencia atual = ocorrenciaDao.buscarStatusParaAtualizar(conexao, idOcorrencia);

                //RN05: so as transicoes permitidas
                if (!atual.podeIrPara(novoStatus)) {
                    throw new RegraNegocioException("RN05: transição de " + atual + " para " + novoStatus + " não é permitida.");
                }

                atualizacao.setIdOcorrencia(idOcorrencia);
                atualizacao.setStatusAnterior(atual);
                atualizacao.setStatusNovo(novoStatus);
                atualizacao.setComentario(comentario);
                atualizacao.setGestor(new Gestor(idGestorLogado)); //RN06: guarda quem fez
                atualizacaoDao.cadastrar(conexao, atualizacao);

                //RN06: data de resolucao preenchida automaticamente quando vira RESOLVIDA
                LocalDate dataResolucao = novoStatus == StatusOcorrencia.RESOLVIDA ? LocalDate.now() : null;
                ocorrenciaDao.atualizarStatus(conexao, idOcorrencia, novoStatus, dataResolucao);

                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        }
        return atualizacaoDao.buscarPorId(idOcorrencia, atualizacao.getId());
    }

    //Corrige so o comentario (o status do historico nao muda)
    public Atualizacao atualizarComentario(Integer idGestorLogado, int idOcorrencia, int id, String comentario) throws SQLException {
        acessoBo.validarGestor(idGestorLogado);
        ocorrenciaDao.buscarPorId(idOcorrencia);
        atualizacaoDao.atualizarComentario(idOcorrencia, id, comentario);
        return atualizacaoDao.buscarPorId(idOcorrencia, id);
    }
}
