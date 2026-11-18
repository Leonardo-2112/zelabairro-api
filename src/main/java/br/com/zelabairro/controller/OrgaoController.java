package br.com.zelabairro.controller;

import java.sql.SQLException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zelabairro.dao.OrgaoDao;
import br.com.zelabairro.dto.OrgaoResponseDto;

//Orgaos so para leitura (vem do seed): o front usa nos formularios de categoria e de gestor
@RestController
@RequestMapping("/orgaos")
public class OrgaoController {

    private final OrgaoDao orgaoDao;
    private final ModelMapper mapper;

    public OrgaoController(OrgaoDao orgaoDao, ModelMapper mapper) {
        this.orgaoDao = orgaoDao;
        this.mapper = mapper;
    }

    @GetMapping
    public List<OrgaoResponseDto> listar() throws SQLException {
        return orgaoDao.listar().stream().map(orgao -> mapper.map(orgao, OrgaoResponseDto.class)).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgaoResponseDto> buscar(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(mapper.map(orgaoDao.buscarPorId(id), OrgaoResponseDto.class));
    }
}
