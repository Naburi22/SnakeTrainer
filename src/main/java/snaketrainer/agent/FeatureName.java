package snaketrainer.agent;

public enum FeatureName {
    DISTANCIA_PARED("DistanciaPared"),
    DISTANCIA_CUERPO("DistanciaCuerpo"),
    LIBERTAD_LOCAL("LibertadLocal"),
    ESPACIO_ACCESIBLE("EspacioAccesible"),
    MEJORA_COMIDA("MejoraComida"),
    COMIDA_EN_FRENTE("ComidaEnFrente"),
    COMIDA_ALCANZABLE("ComidaAlcanzable"),
    SEGUIR_RECTO("SeguirRecto"),
    COLA_ALCANZABLE("ColaAlcanzable");

    private final String displayName;

    FeatureName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static int size() {
        return values().length;
    }
}