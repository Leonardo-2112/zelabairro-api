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

import br.com.zelabairro.bo.CidadaoBo;
import br.com.zelabairro.dto.CidadaoRequestDto;
import br.com.zelabairro.dto.CidadaoResponseDto;
import br.com.zelabairro.dto.CidadaoResumoDto;
import br.com.zelabairro.model.Cidadao;
import jakarta.validation.Valid;

//CRUD de cidadaos. O POST e o cadastro publico (sempre cria cidadao - RN17)
@RestController
@RequestMapping("/cidadaos")
public class CidadaoController {

    private final CidadaoBo cidadaoBo;
    private final ModelMapper mapper;

    public CidadaoController(CidadaoBo cidadaoBo, ModelMapper mapper) {
        this.cidadaoBo = cidadaoBo;
        this.mapper = mapper;
    }

    //RN13: listagem publica so com id e primeiro nome
    @GetMapping
    public List<CidadaoResumoDto> listar() throws SQLException {
        return cidadaoBo.listar().stream().map(cidadao -> {
            CidadaoResumoDto dto = new CidadaoResumoDto();
            dto.setId(cidadao.getId());
            dto.setNome(cidadao.getPrimeiroNome());
            return dto;
        }).toList();
    }

    //Perfil do cidadao (sem a senha)
    @GetMapping("/{id}")
    public ResponseEntity<CidadaoResponseDto> buscar(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(mapper.map(cidadaoBo.buscar(id), CidadaoResponseDto.class));
    }

    //201 + Location | 400 | 409 e-mail repetido (RN02)
    @PostMapping
    public ResponseEntity<CidadaoResponseDto> cadastrar(@Valid @RequestBody CidadaoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Cidadao cidadao = cidadaoBo.cadastrar(mapper.map(dto, Cidadao.class));
        URI uri = builder.path("/cidadaos/{id}").buildAndExpand(cidadao.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(cidadao, CidadaoResponseDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CidadaoResponseDto> atualizar(@PathVariable int id, @Valid @RequestBody CidadaoRequestDto dto) throws SQLException {
        Cidadao cidadao = mapper.map(dto, Cidadao.class);
        cidadao.setId(id);
        return ResponseEntity.ok(mapper.map(cidadaoBo.atualizar(cidadao), CidadaoResponseDto.class));
    }

    //204 | 404 | 409 cidadao com ocorrencias ou apoios
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException {
        cidadaoBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
