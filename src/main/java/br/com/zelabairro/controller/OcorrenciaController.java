package br.com.zelabairro.controller;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.web.bind.annotation.RequestParam;

import br.com.zelabairro.bo.OcorrenciaBo;
import br.com.zelabairro.dto.OcorrenciaConversor;
import br.com.zelabairro.dto.OcorrenciaEdicaoRequestDto;
import br.com.zelabairro.dto.OcorrenciaRequestDto;
import br.com.zelabairro.dto.OcorrenciaResponseDto;
import br.com.zelabairro.model.Bairro;
import br.com.zelabairro.model.Categoria;
import br.com.zelabairro.model.Cidadao;
import br.com.zelabairro.model.Ocorrencia;
import jakarta.validation.Valid;

//CRUD de ocorrencias. Caminho de uma requisicao:
//controller (recebe e valida o DTO) -> BO (regras RNxx) -> DAO (SQL com PreparedStatement) -> Oracle
@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaBo ocorrenciaBo;
    private final OcorrenciaConversor conversor;
    private final ModelMapper mapper;

    public OcorrenciaController(OcorrenciaBo ocorrenciaBo, OcorrenciaConversor conversor, ModelMapper mapper) {
        this.ocorrenciaBo = ocorrenciaBo;
        this.conversor = conversor;
        this.mapper = mapper;
    }

    //Lista ordenada por prioridade (RN10), com filtros opcionais
    @GetMapping
    public List<OcorrenciaResponseDto> listar(@RequestParam(required = false) String status,
                                              @RequestParam(required = false) Integer idBairro,
                                              @RequestParam(required = false) Integer idCategoria) throws SQLException {
        return ocorrenciaBo.listar(status, idBairro, idCategoria).stream().map(conversor::paraDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDto> buscar(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(conversor.paraDto(ocorrenciaBo.buscar(id)));
    }

    //201 + Location | 400 validacao | 404 cidadao, categoria ou bairro inexistente
    @PostMapping
    public ResponseEntity<OcorrenciaResponseDto> cadastrar(@Valid @RequestBody OcorrenciaRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Ocorrencia ocorrencia = mapper.map(dto, Ocorrencia.class); //copia descricao e endereco
        ocorrencia.setCidadao(new Cidadao(dto.getIdCidadao()));
        ocorrencia.setCategoria(new Categoria(dto.getIdCategoria()));
        ocorrencia.setBairro(new Bairro(dto.getIdBairro()));

        Ocorrencia criada = ocorrenciaBo.cadastrar(ocorrencia);
        URI uri = builder.path("/ocorrencias/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(conversor.paraDto(criada));
    }

    //200 | 400 | 404 | 409 ocorrencia que nao esta ABERTA (RN09)
    @PutMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDto> atualizar(@PathVariable int id, @Valid @RequestBody OcorrenciaEdicaoRequestDto dto)
            throws SQLException {
        Ocorrencia ocorrencia = mapper.map(dto, Ocorrencia.class);
        ocorrencia.setId(id);
        ocorrencia.setCategoria(new Categoria(dto.getIdCategoria()));
        ocorrencia.setBairro(new Bairro(dto.getIdBairro()));
        return ResponseEntity.ok(conversor.paraDto(ocorrenciaBo.atualizar(ocorrencia)));
    }

    //204 | 404 | 409 ocorrencia com apoios ou atualizacoes (RN09)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException {
        ocorrenciaBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
