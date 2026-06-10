package snaketrainer.agent;

// TODO: Ajustar niveles de discretización. (spoiler: NO).

public enum FeatureName {
    DISTANCIA_PARED("DistanciaPared", 31),
    DISTANCIA_CUERPO("DistanciaCuerpo", 31),
    LIBERTAD_LOCAL("LibertadLocal", 31),
    ESPACIO_ACCESIBLE("EspacioAccesible", 41),
    MEJORA_COMIDA("MejoraComida", 101),
    COMIDA_EN_FRENTE("ComidaEnFrente", 21),
    COMIDA_ALCANZABLE("ComidaAlcanzable", 21),
    SEGUIR_RECTO("SeguirRecto", 31),
    COLA_ALCANZABLE("ColaAlcanzable", 41),

    DISTANCIA_REAL_COMIDA("DistanciaRealComida", 101),
    PROGRESO_REAL_COMIDA("ProgresoRealComida", 101),
    AREA_SEGURA_RELATIVA("AreaSeguraRelativa", 41),
    ESPACIO_ENCERRADO("EspacioEncerrado", 41),
    DISTANCIA_REAL_COLA("DistanciaRealCola", 41),
    PROGRESO_REAL_COLA("ProgresoRealCola", 41),
    RIESGO_ENCIERRO("RiesgoEncierro", 41),
    COMIDA_SEGURA("ComidaSegura", 21),
    COME_MANZANA("ComeManzana", 21);

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
