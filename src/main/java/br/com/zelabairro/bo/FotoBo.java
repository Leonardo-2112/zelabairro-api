package br.com.zelabairro.bo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.zelabairro.dao.FotoDao;
import br.com.zelabairro.dao.OcorrenciaDao;
import br.com.zelabairro.exception.DadosInvalidosException;
import br.com.zelabairro.exception.EntidadeNaoEncontradaException;
import br.com.zelabairro.exception.RegraNegocioException;
import br.com.zelabairro.model.Foto;
import br.com.zelabairro.model.Ocorrencia;
import br.com.zelabairro.model.enums.StatusOcorrencia;

//Fotos da ocorrencia (RN18): so o autor, so ABERTA, no maximo 3, JPG ou PNG de ate 2 MB
@Service
public class FotoBo {

    private static final int MAX_FOTOS = 3;
    private static final long MAX_BYTES = 2L * 1024 * 1024; //2 MB

    private final FotoDao fotoDao;
    private final OcorrenciaDao ocorrenciaDao;
    private final AcessoBo acessoBo;

    public FotoBo(FotoDao fotoDao, OcorrenciaDao ocorrenciaDao, AcessoBo acessoBo) {
        this.fotoDao = fotoDao;
        this.ocorrenciaDao = ocorrenciaDao;
        this.acessoBo = acessoBo;
    }

    public List<Foto> listar(int idOcorrencia) throws SQLException {
        ocorrenciaDao.buscarPorId(idOcorrencia);
        return fotoDao.listarPorOcorrencia(idOcorrencia);
    }

    public Foto buscarComImagem(int idFoto) throws SQLException {
        return fotoDao.buscarComImagem(idFoto);
    }

    public Foto cadastrar(Integer idCidadaoLogado, int idOcorrencia, MultipartFile arquivo) throws SQLException, IOException {
        Ocorrencia ocorrencia = ocorrenciaDao.buscarPorId(idOcorrencia); //404
        acessoBo.validarAutor(idCidadaoLogado, ocorrencia);              //403

        //400: arquivo vazio, grande demais ou de outro tipo
        if (arquivo == null || arquivo.isEmpty()) {
            throw new DadosInvalidosException("RN18: envie uma foto no campo 'arquivo'.");
        }
        if (arquivo.getSize() > MAX_BYTES) {
            throw new DadosInvalidosException("RN18: a foto deve ter no máximo 2 MB.");
        }
        byte[] bytes = arquivo.getBytes();
        String tipo = tipoDaImagem(bytes);
        if (tipo == null) {
            throw new DadosInvalidosException("RN18: só são aceitas fotos JPG ou PNG.");
        }

        //409: ocorrencia fechada/em andamento ou ja com 3 fotos
        if (ocorrencia.getStatus() != StatusOcorrencia.ABERTA) {
            throw new RegraNegocioException("RN18: só é possível enviar fotos enquanto a ocorrência estiver ABERTA.");
        }
        if (fotoDao.contarPorOcorrencia(idOcorrencia) >= MAX_FOTOS) {
            throw new RegraNegocioException("RN18: a ocorrência já tem o máximo de 3 fotos.");
        }

        Foto foto = new Foto();
        foto.setIdOcorrencia(idOcorrencia);
        foto.setImagem(bytes);
        foto.setTipo(tipo);
        foto.setTamanho(bytes.length);
        fotoDao.cadastrar(foto);
        return fotoDao.buscarPorId(foto.getId());
    }

    public void remover(Integer idCidadaoLogado, int idOcorrencia, int idFoto) throws SQLException {
        Ocorrencia ocorrencia = ocorrenciaDao.buscarPorId(idOcorrencia);
        Foto foto = fotoDao.buscarPorId(idFoto);
        if (foto.getIdOcorrencia() != idOcorrencia) {
            throw new EntidadeNaoEncontradaException("Foto " + idFoto + " não encontrada nesta ocorrência.");
        }
        acessoBo.validarAutor(idCidadaoLogado, ocorrencia);
        if (ocorrencia.getStatus() != StatusOcorrencia.ABERTA) {
            throw new RegraNegocioException("RN18: só é possível remover fotos enquanto a ocorrência estiver ABERTA.");
        }
        fotoDao.remover(idFoto);
    }

    //Confere o tipo pelos primeiros bytes do arquivo (nao confia so na extensao)
    private static String tipoDaImagem(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "image/png";
        }
        return null;
    }
}
