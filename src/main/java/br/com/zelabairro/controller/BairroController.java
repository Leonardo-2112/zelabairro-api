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

import br.com.zelabairro.bo.BairroBo;
import br.com.zelabairro.bo.OcorrenciaBo;
import br.com.zelabairro.dto.BairroRequestDto;
import br.com.zelabairro.dto.BairroResponseDto;
import br.com.zelabairro.dto.OcorrenciaConversor;
import br.com.zelabairro.dto.OcorrenciaResponseDto;
import br.com.zelabairro.model.Bairro;
import jakarta.validation.Valid;

//CRUD de bairros + rota aninhada do 1:N bairro -> ocorrencias
@RestController
@RequestMapping("/bairros")
public class BairroController {

    private final BairroBo bairroBo;
    private final OcorrenciaBo ocorrenciaBo;
    private final OcorrenciaConversor conversor;
    private final ModelMapper mapper;

    public BairroController(BairroBo bairroBo, OcorrenciaBo ocorrenciaBo, OcorrenciaConversor conversor, ModelMapper mapper) {
        this.bairroBo = bairroBo;
        this.ocorrenciaBo = ocorrenciaBo;
        this.conversor = conversor;
        this.mapper = mapper;
    }

    @GetMapping
    public List<BairroResponseDto> listar() throws SQLException {
        return bairroBo.listar().stream().map(bairro -> mapper.map(bairro, BairroResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BairroResponseDto> buscar(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(mapper.map(bairroBo.buscar(id), BairroResponseDto.class));
    }

    //Ocorrencias do bairro, por prioridade (RN10) | 404 bairro inexistente
    @GetMapping("/{id}/ocorrencias")
    public List<OcorrenciaResponseDto> ocorrencias(@PathVariable int id) throws SQLException {
        return ocorrenciaBo.listarPorBairro(id).stream().map(conversor::paraDto).toList();
    }

    //201 + Location | 400 | 409 bairro repetido
    @PostMapping
    public ResponseEntity<BairroResponseDto> cadastrar(@Valid @RequestBody BairroRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Bairro bairro = bairroBo.cadastrar(mapper.map(dto, Bairro.class));
        URI uri = builder.path("/bairros/{id}").buildAndExpand(bairro.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(bairro, BairroResponseDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BairroResponseDto> atualizar(@PathVariable int id, @Valid @RequestBody BairroRequestDto dto) throws SQLException {
        Bairro bairro = mapper.map(dto, Bairro.class);
        bairro.setId(id);
        return ResponseEntity.ok(mapper.map(bairroBo.atualizar(bairro), BairroResponseDto.class));
    }

    //204 | 404 | 409 bairro com ocorrencias (RN14)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException {
        bairroBo.remover(id);
        return ResponseEntity.noContent().build();
    }
}
