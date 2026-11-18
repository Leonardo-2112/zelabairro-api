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

import br.com.zelabairro.bo.CategoriaBo;
import br.com.zelabairro.bo.OcorrenciaBo;
import br.com.zelabairro.dto.CategoriaRequestDto;
import br.com.zelabairro.dto.CategoriaResponseDto;
import br.com.zelabairro.dto.OcorrenciaConversor;
import br.com.zelabairro.dto.OcorrenciaResponseDto;
import br.com.zelabairro.model.Categoria;
import br.com.zelabairro.model.Orgao;
import jakarta.validation.Valid;

//CRUD de categorias + rota aninhada do 1:N categoria -> ocorrencias
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaBo categoriaBo;
    private final OcorrenciaBo ocorrenciaBo;
    private final OcorrenciaConversor conversor;
    private final ModelMapper mapper;

    public CategoriaController(CategoriaBo categoriaBo, OcorrenciaBo ocorrenciaBo, OcorrenciaConversor conversor, ModelMapper mapper) {
        this.categoriaBo = categoriaBo;
        this.ocorrenciaBo = ocorrenciaBo;
        this.conversor = conversor;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CategoriaResponseDto> listar() throws SQLException {
        return categoriaBo.listar().stream().map(this::paraDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> buscar(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(paraDto(categoriaBo.buscar(id)));
    }

    @GetMapping("/{id}/ocorrencias")
    public List<OcorrenciaResponseDto> ocorrencias(@PathVariable int id) throws SQLException {
        return ocorrenciaBo.listarPorCategoria(id).stream().map(conversor::paraDto).toList();
    }

    //201 + Location | 400 | 404 orgao inexistente | 409 nome repetido
    @PostMapping
    public ResponseEntity<CategoriaResponseDto> cadastrar(@Valid @RequestBody CategoriaRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Categoria categoria = categoriaBo.cadastrar(paraModel(dto));
        URI uri = builder.path("/categorias/{id}").buildAndExpand(categoria.getId()).toUri();
        return ResponseEntity.created(uri).body(paraDto(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> atualizar(@PathVariable int id, @Valid @RequestBody CategoriaRequestDto dto) throws SQLException {
        Categoria categoria = paraModel(dto);
        categoria.setId(id);
        return ResponseEntity.ok(paraDto(categoriaBo.atualizar(categoria)));
    }

    //204 | 404 | 409 categoria com ocorrencias (RN14)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException {
        categoriaBo.remover(id);
        return ResponseEntity.noContent().build();
    }

    private Categoria paraModel(CategoriaRequestDto dto) {
        Categoria categoria = mapper.map(dto, Categoria.class);
        categoria.setOrgao(new Orgao(dto.getIdOrgao()));
        return categoria;
    }

    private CategoriaResponseDto paraDto(Categoria categoria) {
        CategoriaResponseDto dto = mapper.map(categoria, CategoriaResponseDto.class);
        dto.setIdOrgao(categoria.getOrgao().getId());
        dto.setOrgao(categoria.getOrgao().getNome());
        return dto;
    }
}
