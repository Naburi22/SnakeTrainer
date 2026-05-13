package snaketrainer.analysis;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import snaketrainer.agent.FeatureName;
import snaketrainer.agent.FeatureVector;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;
import snaketrainer.model.Position;

public final class FeatureExtractor {
    private FeatureExtractor() {
    }

    public static FeatureVector extract(Cell[][] board, Direction currentDirection, Direction candidateDirection) {
        Position head = findCell(board, Cell.SNAKE_HEAD);
        Position apple = findCell(board, Cell.APPLE);
        Position tail = findCell(board, Cell.SNAKE_TAIL);

        if (head == null) {
            return new FeatureVector(new double[FeatureName.size()]);
        }

        Position newHead = nextPosition(head, candidateDirection);

        int rows = board.length;
        int cols = board[0].length;
        int totalCells = rows * cols;
        int maxDistance = rows + cols;

        int reachableCells = countReachableCells(board, newHead);
        int snakeLength = countSnakeLength(board);
        int freeCells = countFreeCells(board);

        int oldFoodDistance = bfsDistance(board, head, apple);
        int newFoodDistance = bfsDistance(board, newHead, apple);
        int oldTailDistance = bfsDistance(board, head, tail);
        int newTailDistance = bfsDistance(board, newHead, tail);

        double distanciaPared = distanceToNearestWall(newHead, rows, cols) / (double) Math.max(rows, cols);
        double distanciaCuerpo = distanceToNearestBody(board, newHead, maxDistance) / (double) maxDistance;
        double libertadLocal = countLocalFreedom(board, newHead) / 4.0;
        double espacioAccesible = reachableCells / (double) totalCells;
        double mejoraComida = foodImprovement(head, newHead, apple) / (double) maxDistance;
        double comidaEnFrente = isFoodInFront(newHead, apple, candidateDirection) ? 1.0 : 0.0;
        double comidaAlcanzable = apple != null && newFoodDistance >= 0 ? 1.0 : 0.0;
        double seguirRecto = candidateDirection == currentDirection ? 1.0 : 0.0;
        double colaAlcanzable = tail != null && newTailDistance >= 0 ? 1.0 : 0.0;

        double distanciaRealComida = normalizeDistance(newFoodDistance, maxDistance);
        double progresoRealComida = normalizeProgress(oldFoodDistance, newFoodDistance, maxDistance);
        double areaSeguraRelativa = calculateSafeAreaRatio(reachableCells, snakeLength);
        double espacioEncerrado = calculateTrappedSpaceRatio(freeCells, reachableCells);
        double distanciaRealCola = normalizeDistance(newTailDistance, maxDistance);
        double progresoRealCola = normalizeProgress(oldTailDistance, newTailDistance, maxDistance);
        double riesgoEncierro = calculateTrapRisk(reachableCells, snakeLength);
        double comidaSegura = comidaAlcanzable == 1.0 && colaAlcanzable == 1.0 ? 1.0 : 0.0;
        double comeManzana = apple != null && newHead.equals(apple) ? 1.0 : 0.0;

        double[] values = new double[FeatureName.size()];

        values[FeatureName.DISTANCIA_PARED.ordinal()] = distanciaPared;
        values[FeatureName.DISTANCIA_CUERPO.ordinal()] = distanciaCuerpo;
        values[FeatureName.LIBERTAD_LOCAL.ordinal()] = libertadLocal;
        values[FeatureName.ESPACIO_ACCESIBLE.ordinal()] = espacioAccesible;
        values[FeatureName.MEJORA_COMIDA.ordinal()] = mejoraComida;
        values[FeatureName.COMIDA_EN_FRENTE.ordinal()] = comidaEnFrente;
        values[FeatureName.COMIDA_ALCANZABLE.ordinal()] = comidaAlcanzable;
        values[FeatureName.SEGUIR_RECTO.ordinal()] = seguirRecto;
        values[FeatureName.COLA_ALCANZABLE.ordinal()] = colaAlcanzable;
        values[FeatureName.DISTANCIA_REAL_COMIDA.ordinal()] = distanciaRealComida;
        values[FeatureName.PROGRESO_REAL_COMIDA.ordinal()] = progresoRealComida;
        values[FeatureName.AREA_SEGURA_RELATIVA.ordinal()] = areaSeguraRelativa;
        values[FeatureName.ESPACIO_ENCERRADO.ordinal()] = espacioEncerrado;
        values[FeatureName.DISTANCIA_REAL_COLA.ordinal()] = distanciaRealCola;
        values[FeatureName.PROGRESO_REAL_COLA.ordinal()] = progresoRealCola;
        values[FeatureName.RIESGO_ENCIERRO.ordinal()] = riesgoEncierro;
        values[FeatureName.COMIDA_SEGURA.ordinal()] = comidaSegura;
        values[FeatureName.COME_MANZANA.ordinal()] = comeManzana;

        return new FeatureVector(values);
    }

    public static Position findCell(Cell[][] board, Cell target) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == target) {
                    return new Position(row, col);
                }
            }
        }

        return null;
    }

    public static Position nextPosition(Position position, Direction direction) {
        return switch (direction) {
            case UP -> new Position(position.row() - 1, position.col());
            case DOWN -> new Position(position.row() + 1, position.col());
            case LEFT -> new Position(position.row(), position.col() - 1);
            case RIGHT -> new Position(position.row(), position.col() + 1);
        };
    }

    public static boolean isInside(Cell[][] board, Position position) {
        return position.row() >= 0
                && position.row() < board.length
                && position.col() >= 0
                && position.col() < board[0].length;
    }

    public static boolean isSafeImmediateMove(Cell[][] board, Position position) {
        if (!isInside(board, position)) {
            return false;
        }

        Cell cell = board[position.row()][position.col()];

        return cell != Cell.SNAKE_HEAD && cell != Cell.SNAKE_BODY;
    }

    private static boolean isPassableForSearch(Cell[][] board, Position position) {
        if (!isInside(board, position)) {
            return false;
        }

        Cell cell = board[position.row()][position.col()];

        return cell == Cell.EMPTY
                || cell == Cell.APPLE
                || cell == Cell.SNAKE_TAIL;
    }

    private static int distanceToNearestWall(Position position, int rows, int cols) {
        if (position.row() < 0 || position.row() >= rows || position.col() < 0 || position.col() >= cols) {
            return 0;
        }

        int up = position.row();
        int down = rows - 1 - position.row();
        int left = position.col();
        int right = cols - 1 - position.col();

        return Math.min(Math.min(up, down), Math.min(left, right));
    }

    private static int distanceToNearestBody(Cell[][] board, Position position, int defaultDistance) {
        if (!isInside(board, position)) {
            return 0;
        }

        int best = defaultDistance;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == Cell.SNAKE_BODY || board[row][col] == Cell.SNAKE_HEAD) {
                    int distance = manhattan(position, new Position(row, col));

                    if (distance > 0) {
                        best = Math.min(best, distance);
                    }
                }
            }
        }

        return best;
    }

    private static int countLocalFreedom(Cell[][] board, Position position) {
        int count = 0;

        for (Direction direction : Direction.values()) {
            Position next = nextPosition(position, direction);

            if (isPassableForSearch(board, next)) {
                count++;
            }
        }

        return count;
    }

    private static int countReachableCells(Cell[][] board, Position start) {
        return bfsCount(board, start, null);
    }

    private static int bfsCount(Cell[][] board, Position start, Position target) {
        if (!isInside(board, start)) {
            return 0;
        }

        Queue<Position> queue = new ArrayDeque<>();
        Set<Position> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        int count = 0;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            count++;

            if (target != null && current.equals(target)) {
                return count;
            }

            for (Direction direction : Direction.values()) {
                Position next = nextPosition(current, direction);

                if (!isInside(board, next) || visited.contains(next)) {
                    continue;
                }

                if (target != null && next.equals(target)) {
                    visited.add(next);
                    queue.add(next);
                } else if (isPassableForSearch(board, next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        return target == null ? count : 0;
    }

    private static int bfsDistance(Cell[][] board, Position start, Position target) {
        if (target == null || !isInside(board, start)) {
            return -1;
        }

        Queue<Position> queue = new ArrayDeque<>();
        Set<Position> visited = new HashSet<>();
        Map<Position, Integer> distances = new HashMap<>();

        queue.add(start);
        visited.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.equals(target)) {
                return distances.get(current);
            }

            for (Direction direction : Direction.values()) {
                Position next = nextPosition(current, direction);

                if (!isInside(board, next) || visited.contains(next)) {
                    continue;
                }

                if (next.equals(target) || isPassableForSearch(board, next)) {
                    visited.add(next);
                    distances.put(next, distances.get(current) + 1);
                    queue.add(next);
                }
            }
        }

        return -1;
    }

    private static int countSnakeLength(Cell[][] board) {
        int count = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == Cell.SNAKE_HEAD
                        || board[row][col] == Cell.SNAKE_BODY
                        || board[row][col] == Cell.SNAKE_TAIL) {
                    count++;
                }
            }
        }

        return count;
    }

    private static int countFreeCells(Cell[][] board) {
        int count = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == Cell.EMPTY
                        || board[row][col] == Cell.APPLE
                        || board[row][col] == Cell.SNAKE_TAIL) {
                    count++;
                }
            }
        }

        return count;
    }

    private static double normalizeDistance(int distance, int maxDistance) {
        if (distance < 0) {
            return 1.0;
        }

        return Math.min(1.0, distance / (double) maxDistance);
    }

    private static double normalizeProgress(int oldDistance, int newDistance, int maxDistance) {
        if (oldDistance < 0 && newDistance < 0) {
            return 0.0;
        }

        if (oldDistance < 0) {
            oldDistance = maxDistance;
        }

        if (newDistance < 0) {
            newDistance = maxDistance;
        }

        return (oldDistance - newDistance) / (double) maxDistance;
    }

    private static double calculateSafeAreaRatio(int reachableCells, int snakeLength) {
        if (snakeLength <= 0) {
            return 0.0;
        }

        return Math.min(1.0, reachableCells / (double) snakeLength);
    }

    private static double calculateTrappedSpaceRatio(int freeCells, int reachableCells) {
        if (freeCells <= 0) {
            return 0.0;
        }

        return Math.max(0.0, (freeCells - reachableCells) / (double) freeCells);
    }

    private static double calculateTrapRisk(int reachableCells, int snakeLength) {
        if (snakeLength <= 0) {
            return 1.0;
        }

        double desiredSpace = snakeLength * 2.0;
        double ratio = reachableCells / desiredSpace;

        return 1.0 - Math.min(1.0, ratio);
    }

    private static int foodImprovement(Position oldHead, Position newHead, Position apple) {
        if (apple == null) {
            return 0;
        }

        return manhattan(oldHead, apple) - manhattan(newHead, apple);
    }

    private static boolean isFoodInFront(Position newHead, Position apple, Direction direction) {
        if (apple == null) {
            return false;
        }

        return switch (direction) {
            case UP -> apple.col() == newHead.col() && apple.row() < newHead.row();
            case DOWN -> apple.col() == newHead.col() && apple.row() > newHead.row();
            case LEFT -> apple.row() == newHead.row() && apple.col() < newHead.col();
            case RIGHT -> apple.row() == newHead.row() && apple.col() > newHead.col();
        };
    }

    private static int manhattan(Position a, Position b) {
        return Math.abs(a.row() - b.row()) + Math.abs(a.col() - b.col());
    }
}
