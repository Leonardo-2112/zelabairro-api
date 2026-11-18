package br.com.zelabairro.model.enums;

//RN04: status possiveis de uma ocorrencia
public enum StatusOcorrencia {
    ABERTA, EM_ANDAMENTO, RESOLVIDA, CANCELADA;

    //RN05: transicoes permitidas. RESOLVIDA e CANCELADA sao finais
    public boolean podeIrPara(StatusOcorrencia novo) {
        return switch (this) {
            case ABERTA -> novo == EM_ANDAMENTO || novo == CANCELADA;
            case EM_ANDAMENTO -> novo == RESOLVIDA || novo == CANCELADA;
            default -> false;
        };
    }

    //Ocorrencia ainda em aberto (tem prioridade e pode receber apoio)
    public boolean isEmAberto() {
        return this == ABERTA || this == EM_ANDAMENTO;
    }
}
