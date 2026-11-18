package br.com.zelabairro.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zelabairro.bo.AcessoBo;
import br.com.zelabairro.bo.AtualizacaoBo;
import br.com.zelabairro.dto.AtualizacaoRequestDto;
import br.com.zelabairro.dto.AtualizacaoResponseDto;
import br.com.zelabairro.dto.ComentarioRequestDto;
import br.com.zelabairro.model.Atualizacao;
import br.com.zelabairro.model.enums.StatusOcorrencia;
import jakarta.validation.Valid;

//Rotas aninhadas do 1:N ocorrencia -> atualizacoes (historico de status)
@RestController
@RequestMapping("/ocorrencias/{idOcorrencia}/atualizacoes")
public class AtualizacaoController {

    private final AtualizacaoBo atualizacaoBo;
    private final ModelMapper mapper;

    public AtualizacaoController(AtualizacaoBo atualizacaoBo, ModelMapper mapper) {
        this.atualizacaoBo = atualizacaoBo;
        this.mapper = mapper;
    }

    //Historico publico | 404 ocorrencia inexistente
    @GetMapping
    public List<AtualizacaoResponseDto> listar(@PathVariable int idOcorrencia) throws SQLException {
        return atualizacaoBo.listar(idOcorrencia).stream().map(this::paraDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtualizacaoResponseDto> buscar(@PathVariable int idOcorrencia, @PathVariable int id) throws SQLException {
        return ResponseEntity.ok(paraDto(atualizacaoBo.buscar(idOcorrencia, id)));
    }

    //So gestor muda o status (RN16): 201 + Location | 400 | 403 | 404 | 409 transicao invalida (RN05)
    @PostMapping
    public ResponseEntity<AtualizacaoResponseDto> cadastrar(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor,
                                                            @PathVariable int idOcorrencia,
                                                            @Valid @RequestBody AtualizacaoRequestDto dto,
                                                            UriComponentsBuilder builder) throws SQLException {
        Atualizacao atualizacao = atualizacaoBo.cadastrar(idGestor, idOcorrencia,
                StatusOcorrencia.valueOf(dto.getStatus()), dto.getComentario());
        URI uri = builder.path("/ocorrencias/{idOcorrencia}/atualizacoes/{id}")
                .buildAndExpand(idOcorrencia, atualizacao.getId()).toUri();
        return ResponseEntity.created(uri).body(paraDto(atualizacao));
    }

    //Gestor corrige o comentario de uma atualizacao (o status nao muda)
    @PutMapping("/{id}")
    public ResponseEntity<AtualizacaoResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor,
                                                            @PathVariable int idOcorrencia, @PathVariable int id,
                                                            @Valid @RequestBody ComentarioRequestDto dto) throws SQLException {
        return ResponseEntity.ok(paraDto(atualizacaoBo.atualizarComentario(idGestor, idOcorrencia, id, dto.getComentario())));
    }

    private AtualizacaoResponseDto paraDto(Atualizacao atualizacao) {
        AtualizacaoResponseDto dto = mapper.map(atualizacao, AtualizacaoResponseDto.class);
        dto.setStatusAnterior(atualizacao.getStatusAnterior().name());
        dto.setStatusNovo(atualizacao.getStatusNovo().name());
        dto.setIdGestor(atualizacao.getGestor().getId());
        dto.setGestor(atualizacao.getGestor().getNome());
        return dto;
    }
}
