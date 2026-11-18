package br.com.zelabairro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Entrada do POST /ocorrencias/{id}/atualizacoes (so gestor - RN16)
public class AtualizacaoRequestDto {

    @NotBlank(message = "Informe o novo status.")
    @Pattern(regexp = "ABERTA|EM_ANDAMENTO|RESOLVIDA|CANCELADA", message = "RN04: status deve ser ABERTA, EM_ANDAMENTO, RESOLVIDA ou CANCELADA.")
    private String status;

    @Size(max = 300, message = "O comentário deve ter no máximo 300 caracteres.")
    private String comentario;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
