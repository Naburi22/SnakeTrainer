package snaketrainer.evolution.evaluation;

public enum EndCause {
    DEATH("Muerte"),
    NO_APPLE_TIMEOUT("Demasiados pasos sin comer");

    private final String displayName;

    EndCause(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}