package br.gov.caixa.domain.enums;

public enum PerfilInvestidor {
    CONSERVADOR,
    MODERADO,
    AGRESSIVO;

    public Risco riscoPadrao() {
        return switch (this) {
            case CONSERVADOR -> Risco.BAIXO;
            case MODERADO -> Risco.MEDIO;
            case AGRESSIVO -> Risco.ALTO;
        };
    }

    public static PerfilInvestidor fromString(String perfil) {
        return switch (perfil.toLowerCase()) {
            case "conservador" -> CONSERVADOR;
            case "moderado" -> MODERADO;
            case "agressivo" -> AGRESSIVO;
            default -> MODERADO;
        };
    }

    public static boolean isValid(String perfil) {
        return perfil.equalsIgnoreCase("conservador") ||
               perfil.equalsIgnoreCase("moderado") ||
               perfil.equalsIgnoreCase("agressivo");
    }
}
