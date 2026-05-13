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
    COLA_ALCANZABLE("ColaAlcanzable"),

    DISTANCIA_REAL_COMIDA("DistanciaRealComida"),
    PROGRESO_REAL_COMIDA("ProgresoRealComida"),
    AREA_SEGURA_RELATIVA("AreaSeguraRelativa"),
    ESPACIO_ENCERRADO("EspacioEncerrado"),
    DISTANCIA_REAL_COLA("DistanciaRealCola"),
    PROGRESO_REAL_COLA("ProgresoRealCola"),
    RIESGO_ENCIERRO("RiesgoEncierro"),
    COMIDA_SEGURA("ComidaSegura"),
    COME_MANZANA("ComeManzana");

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
