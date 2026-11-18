package br.com.zelabairro.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zelabairro.bo.AcessoBo;
import br.com.zelabairro.bo.PainelBo;
import br.com.zelabairro.dto.PainelBairroDto;

//Painel do gestor por bairro (RN16): 200 | 403
@RestController
@RequestMapping("/painel")
public class PainelController {

    private final PainelBo painelBo;

    public PainelController(PainelBo painelBo) {
        this.painelBo = painelBo;
    }

    @GetMapping("/bairros")
    public List<PainelBairroDto> porBairro(@RequestHeader(value = AcessoBo.HEADER_GESTOR, required = false) Integer idGestor)
            throws SQLException {
        return painelBo.porBairro(idGestor);
    }
}
