package br.com.zelabairro.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zelabairro.bo.ApoioBo;
import br.com.zelabairro.dto.ApoioRequestDto;
import br.com.zelabairro.dto.ApoioResponseDto;
import br.com.zelabairro.model.Apoio;
import jakarta.validation.Valid;

//Apoios de uma ocorrencia (N:N cidadao <-> ocorrencia pela tabela associativa tb_apoio)
@RestController
@RequestMapping("/ocorrencias/{idOcorrencia}/apoios")
public class ApoioController {

    private final ApoioBo apoioBo;

    public ApoioController(ApoioBo apoioBo) {
        this.apoioBo = apoioBo;
    }

    @GetMapping
    public List<ApoioResponseDto> listar(@PathVariable int idOcorrencia) throws SQLException {
        return apoioBo.listar(idOcorrencia).stream().map(this::paraDto).toList();
    }

    @GetMapping("/{idCidadao}")
    public ResponseEntity<ApoioResponseDto> buscar(@PathVariable int idOcorrencia, @PathVariable int idCidadao) throws SQLException {
        return ResponseEntity.ok(paraDto(apoioBo.buscar(idOcorrencia, idCidadao)));
    }

    //201 + Location | 400 | 404 | 409 apoio repetido, proprio ou ocorrencia fechada (RN08)
    @PostMapping
    public ResponseEntity<ApoioResponseDto> cadastrar(@PathVariable int idOcorrencia, @Valid @RequestBody ApoioRequestDto dto,
                                                      UriComponentsBuilder builder) throws SQLException {
        Apoio apoio = apoioBo.cadastrar(idOcorrencia, dto.getIdCidadao());
        URI uri = builder.path("/ocorrencias/{idOcorrencia}/apoios/{idCidadao}")
                .buildAndExpand(idOcorrencia, dto.getIdCidadao()).toUri();
        return ResponseEntity.created(uri).body(paraDto(apoio));
    }

    //Retirar o apoio: 204 | 404
    @DeleteMapping("/{idCidadao}")
    public ResponseEntity<Void> remover(@PathVariable int idOcorrencia, @PathVariable int idCidadao) throws SQLException {
        apoioBo.remover(idOcorrencia, idCidadao);
        return ResponseEntity.noContent().build();
    }

    //RN13: so o primeiro nome de quem apoia
    private ApoioResponseDto paraDto(Apoio apoio) {
        ApoioResponseDto dto = new ApoioResponseDto();
        dto.setIdOcorrencia(apoio.getIdOcorrencia());
        dto.setIdCidadao(apoio.getCidadao().getId());
        dto.setCidadao(apoio.getCidadao().getPrimeiroNome());
        dto.setDataApoio(apoio.getData());
        return dto;
    }
}
