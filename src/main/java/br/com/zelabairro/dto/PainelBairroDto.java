package br.com.zelabairro.dto;

//Linha do painel do gestor: ocorrencias por bairro (GET /painel/bairros)
public class PainelBairroDto {

    private Integer idBairro;
    private String bairro;
    private String subprefeitura;
    private Integer abertas;
    private Integer emAndamento;
    private Integer atrasadas;
    private Integer resolvidas;
    private Integer canceladas;
    private Integer total;

    public Integer getIdBairro() { return idBairro; }
    public void setIdBairro(Integer idBairro) { this.idBairro = idBairro; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getSubprefeitura() { return subprefeitura; }
    public void setSubprefeitura(String subprefeitura) { this.subprefeitura = subprefeitura; }
    public Integer getAbertas() { return abertas; }
    public void setAbertas(Integer abertas) { this.abertas = abertas; }
    public Integer getEmAndamento() { return emAndamento; }
    public void setEmAndamento(Integer emAndamento) { this.emAndamento = emAndamento; }
    public Integer getAtrasadas() { return atrasadas; }
    public void setAtrasadas(Integer atrasadas) { this.atrasadas = atrasadas; }
    public Integer getResolvidas() { return resolvidas; }
    public void setResolvidas(Integer resolvidas) { this.resolvidas = resolvidas; }
    public Integer getCanceladas() { return canceladas; }
    public void setCanceladas(Integer canceladas) { this.canceladas = canceladas; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
}
