package br.com.zelabairro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Entrada do PUT /ocorrencias/{id}/atualizacoes/{idAtualizacao}: corrige so o comentario
public class ComentarioRequestDto {

    @NotBlank(message = "Informe o comentário.")
    @Size(max = 300, message = "O comentário deve ter no máximo 300 caracteres.")
    private String comentario;

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
