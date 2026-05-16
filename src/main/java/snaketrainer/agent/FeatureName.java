package snaketrainer.agent;

// TODO: Ajustar niveles de discretización.

public enum FeatureName {
    DISTANCIA_PARED("DistanciaPared", 7),
    DISTANCIA_CUERPO("DistanciaCuerpo", 7),
    LIBERTAD_LOCAL("LibertadLocal", 7),
    ESPACIO_ACCESIBLE("EspacioAccesible", 9),
    MEJORA_COMIDA("MejoraComida", 11),
    COMIDA_EN_FRENTE("ComidaEnFrente", 5),
    COMIDA_ALCANZABLE("ComidaAlcanzable", 5),
    SEGUIR_RECTO("SeguirRecto", 7),
    COLA_ALCANZABLE("ColaAlcanzable", 9),

    DISTANCIA_REAL_COMIDA("DistanciaRealComida", 11),
    PROGRESO_REAL_COMIDA("ProgresoRealComida", 11),
    AREA_SEGURA_RELATIVA("AreaSeguraRelativa", 9),
    ESPACIO_ENCERRADO("EspacioEncerrado", 9),
    DISTANCIA_REAL_COLA("DistanciaRealCola", 9),
    PROGRESO_REAL_COLA("ProgresoRealCola", 9),
    RIESGO_ENCIERRO("RiesgoEncierro", 9),
    COMIDA_SEGURA("ComidaSegura", 5),
    COME_MANZANA("ComeManzana", 5);

    private final String displayName;
    private final int discretizationLevels;

    FeatureName(String displayName, int discretizationLevels) {
        this.displayName = displayName;
        this.discretizationLevels = discretizationLevels;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDiscretizationLevels() {
        return discretizationLevels;
    }

    public static int size() {
        return values().length;
    }
}
