package br.com.zelabairro.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zelabairro.bo.AcessoBo;
import br.com.zelabairro.bo.FotoBo;
import br.com.zelabairro.dto.FotoResponseDto;
import br.com.zelabairro.model.Foto;

//Fotos da ocorrencia (RN18). O arquivo chega como multipart/form-data no campo "arquivo"
//e vai para o BLOB tb_foto.bl_imagem (o disco do Render nao guarda arquivos)
@RestController
public class FotoController {

    private final FotoBo fotoBo;
    private final ModelMapper mapper;

    public FotoController(FotoBo fotoBo, ModelMapper mapper) {
        this.fotoBo = fotoBo;
        this.mapper = mapper;
    }

    //Lista sem os bytes; cada item traz a url da imagem
    @GetMapping("/ocorrencias/{idOcorrencia}/fotos")
    public List<FotoResponseDto> listar(@PathVariable int idOcorrencia) throws SQLException {
        return fotoBo.listar(idOcorrencia).stream().map(this::paraDto).toList();
    }

    //So o autor (X-Cidadao-Id): 201 + Location | 400 tipo/tamanho | 403 | 404 | 409 nao ABERTA ou 4a foto
    @PostMapping(value = "/ocorrencias/{idOcorrencia}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FotoResponseDto> enviar(@RequestHeader(value = AcessoBo.HEADER_CIDADAO, required = false) Integer idCidadao,
                                                  @PathVariable int idOcorrencia,
                                                  @RequestParam("arquivo") MultipartFile arquivo,
                                                  UriComponentsBuilder builder) throws SQLException, IOException {
        Foto foto = fotoBo.cadastrar(idCidadao, idOcorrencia, arquivo);
        URI uri = builder.path("/fotos/{id}").buildAndExpand(foto.getId()).toUri();
        return ResponseEntity.created(uri).body(paraDto(foto));
    }

    //Devolve a propria imagem (o front usa em <img src=...>)
    @GetMapping("/fotos/{idFoto}")
    public ResponseEntity<byte[]> imagem(@PathVariable int idFoto) throws SQLException {
        Foto foto = fotoBo.buscarComImagem(idFoto);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.getTipo()))
                .cacheControl(CacheControl.noCache())
                .body(foto.getImagem());
    }

    @DeleteMapping("/ocorrencias/{idOcorrencia}/fotos/{idFoto}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER_CIDADAO, required = false) Integer idCidadao,
                                        @PathVariable int idOcorrencia, @PathVariable int idFoto) throws SQLException {
        fotoBo.remover(idCidadao, idOcorrencia, idFoto);
        return ResponseEntity.noContent().build();
    }

    private FotoResponseDto paraDto(Foto foto) {
        FotoResponseDto dto = mapper.map(foto, FotoResponseDto.class);
        dto.setUrl("/fotos/" + foto.getId());
        return dto;
    }
}
