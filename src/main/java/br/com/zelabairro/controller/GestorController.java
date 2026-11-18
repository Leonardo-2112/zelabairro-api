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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zelabairro.bo.AcessoBo;
import br.com.zelabairro.bo.GestorBo;
import br.com.zelabairro.dto.GestorRequestDto;
import br.com.zelabairro.dto.GestorResponseDto;
import br.com.zelabairro.model.Gestor;
import br.com.zelabairro.model.Orgao;
import jakarta.validation.Valid;

//CRUD de gestores: todas as rotas exigem o cabecalho X-Gestor-Id (RN16); 403 se nao for gestor
@RestController
@RequestMapping("/gestores")
public class GestorController {

    private final GestorBo gestorBo;
    private final ModelMapper mapper;

    public GestorController(GestorBo gestorBo, ModelMapper mapper) {
        this.gestorBo = gestorBo;
        this.mapper = mapper;
    }

    @GetMapping
    public List<GestorResponseDto> listar(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor)
            throws SQLException {
        return gestorBo.listar(idGestor).stream().map(this::paraDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GestorResponseDto> buscar(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor,
                                                    @PathVariable int id) throws SQLException {
        return ResponseEntity.ok(paraDto(gestorBo.buscar(idGestor, id)));
    }

    //RN17: um gestor cadastra outro
    @PostMapping
    public ResponseEntity<GestorResponseDto> cadastrar(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor,
                                                       @Valid @RequestBody GestorRequestDto dto, UriComponentsBuilder builder) throws SQLException {
        Gestor gestor = gestorBo.cadastrar(idGestor, paraModel(dto));
        URI uri = builder.path("/gestores/{id}").buildAndExpand(gestor.getId()).toUri();
        return ResponseEntity.created(uri).body(paraDto(gestor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GestorResponseDto> atualizar(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor,
                                                       @PathVariable int id, @Valid @RequestBody GestorRequestDto dto) throws SQLException {
        Gestor gestor = paraModel(dto);
        gestor.setId(id);
        return ResponseEntity.ok(paraDto(gestorBo.atualizar(idGestor, gestor)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor,
                                        @PathVariable int id) throws SQLException {
        gestorBo.remover(idGestor, id);
        return ResponseEntity.noContent().build();
    }

    private Gestor paraModel(GestorRequestDto dto) {
        Gestor gestor = mapper.map(dto, Gestor.class);
        gestor.setOrgao(new Orgao(dto.getIdOrgao()));
        return gestor;
    }

    private GestorResponseDto paraDto(Gestor gestor) {
        GestorResponseDto dto = mapper.map(gestor, GestorResponseDto.class);
        dto.setIdOrgao(gestor.getOrgao().getId());
        dto.setOrgao(gestor.getOrgao().getNome());
        return dto;
    }
}
