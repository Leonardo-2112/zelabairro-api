package br.com.zelabairro.model.enums;

//RN10: classe da prioridade a partir da pontuacao
public enum Prioridade {
    ALTA, MEDIA, BAIXA;

    public static Prioridade daPontuacao(int pontos) {
        if (pontos >= 70) return ALTA;
        if (pontos >= 40) return MEDIA;
        return BAIXA;
    }
}
