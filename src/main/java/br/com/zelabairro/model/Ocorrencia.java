package br.com.zelabairro.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import br.com.zelabairro.model.enums.Prioridade;
import br.com.zelabairro.model.enums.StatusOcorrencia;

//tb_ocorrencia: o problema registrado pelo cidadao (RN01: 1 autor, 1 categoria, 1 bairro)
public class Ocorrencia {

    private int id;
    private String descricao;
    private String endereco;
    private StatusOcorrencia status;
    private LocalDate dataAbertura;
    private LocalDate dataResolucao;
    private Integer prazoPrevisto;   //dias previstos pela IA (nulo: RN12)
    private String riscoAtraso;      //'S' ou 'N' (nulo: RN12)
    private Cidadao cidadao;         //autor
    private Categoria categoria;
    private Bairro bairro;
    private int qtdApoios;           //calculado na consulta
    private int qtdFotos;            //calculado na consulta

    public Ocorrencia() {
    }

    public Ocorrencia(int id) {
        this.id = id;
    }

    //Dias inteiros entre a abertura e hoje (ou a resolucao, se ja resolvida)
    public long getDiasEmAberto() {
        LocalDate fim = dataResolucao != null ? dataResolucao : LocalDate.now();
        return ChronoUnit.DAYS.between(dataAbertura, fim);
    }

    //RN11: atrasada = dias em aberto maior que o prazo de referencia da categoria
    public boolean isAtrasada() {
        if (status == StatusOcorrencia.CANCELADA) return false;
        return getDiasEmAberto() > categoria.getPrazoDias();
    }

    //RN10: pontuacao de prioridade (so para ABERTA e EM_ANDAMENTO; senao null)
    public Integer getPontuacaoPrioridade() {
        if (!status.isEmAberto()) return null;
        long pontos = categoria.getGravidade() * 10L
                + Math.min(qtdApoios, 20) * 2L
                + Math.min(getDiasEmAberto(), 30);
        if ("S".equals(riscoAtraso) || isAtrasada()) {
            pontos += 15; //soma uma vez so
        }
        return (int) pontos;
    }

    //RN10: classe ALTA (>= 70), MEDIA (40 a 69) ou BAIXA (< 40)
    public Prioridade getPrioridade() {
        Integer pontos = getPontuacaoPrioridade();
        return pontos == null ? null : Prioridade.daPontuacao(pontos);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public StatusOcorrencia getStatus() { return status; }
    public void setStatus(StatusOcorrencia status) { this.status = status; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDate getDataResolucao() { return dataResolucao; }
    public void setDataResolucao(LocalDate dataResolucao) { this.dataResolucao = dataResolucao; }
    public Integer getPrazoPrevisto() { return prazoPrevisto; }
    public void setPrazoPrevisto(Integer prazoPrevisto) { this.prazoPrevisto = prazoPrevisto; }
    public String getRiscoAtraso() { return riscoAtraso; }
    public void setRiscoAtraso(String riscoAtraso) { this.riscoAtraso = riscoAtraso; }
    public Cidadao getCidadao() { return cidadao; }
    public void setCidadao(Cidadao cidadao) { this.cidadao = cidadao; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Bairro getBairro() { return bairro; }
    public void setBairro(Bairro bairro) { this.bairro = bairro; }
    public int getQtdApoios() { return qtdApoios; }
    public void setQtdApoios(int qtdApoios) { this.qtdApoios = qtdApoios; }
    public int getQtdFotos() { return qtdFotos; }
    public void setQtdFotos(int qtdFotos) { this.qtdFotos = qtdFotos; }
}
