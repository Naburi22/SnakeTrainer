package snaketrainer.analysis;

import java.util.Arrays;
import snaketrainer.agent.FeatureName;
import snaketrainer.agent.FeatureVector;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;
import snaketrainer.model.Position;

public final class FeatureExtractor {
    private static final Direction[] DIRECTIONS = Direction.values();

    private FeatureExtractor() {
    }

    public static DecisionContext createDecisionContext(Cell[][] board, Direction currentDirection, int score) {
        return new DecisionContext(board, currentDirection, score);
    }

    public static FeatureVector extract(Cell[][] board, Direction currentDirection, Direction candidateDirection, int score) {
        return extract(createDecisionContext(board, currentDirection, score), candidateDirection);
    }

    public static FeatureVector extract(DecisionContext context, Direction candidateDirection) {
        if (!context.hasHead()) {
            return new FeatureVector(new double[FeatureName.size()]);
        }

        int newHeadId = context.nextId(context.headId, candidateDirection);
        int newRow = context.rowOf(newHeadId);
        int newCol = context.colOf(newHeadId);

        BfsResult candidateSearch = context.runBfs(newHeadId);

        int reachableCells = candidateSearch.reachableCells;
        int newFoodDistance = candidateSearch.distanceToApple;
        int newTailDistance = candidateSearch.distanceToTail;

        double distanciaPared = distanceToNearestWall(newRow, newCol, context.rows, context.cols)
                / (double) Math.max(context.rows, context.cols);
        double distanciaCuerpo = context.distanceToNearestBody(newHeadId) / (double) context.maxDistance;
        double libertadLocal = context.countLocalFreedom(newHeadId) / 4.0;
        double espacioAccesible = reachableCells / (double) context.totalCells;
        double mejoraComida = context.foodImprovement(newHeadId) / (double) context.maxDistance;
        double comidaEnFrente = context.isFoodInFront(newHeadId, candidateDirection) ? 1.0 : 0.0;
        double comidaAlcanzable = context.hasApple() && newFoodDistance >= 0 ? 1.0 : 0.0;
        double seguirRecto = candidateDirection == context.currentDirection ? 1.0 : 0.0;
        double colaAlcanzable = context.hasTail() && newTailDistance >= 0 ? 1.0 : 0.0;

        double distanciaRealComida = normalizeDistance(newFoodDistance, context.maxDistance);
        double progresoRealComida = normalizeProgress(context.oldFoodDistance, newFoodDistance, context.maxDistance);
        double areaSeguraRelativa = calculateSafeAreaRatio(reachableCells, context.snakeLength);
        double espacioEncerrado = calculateTrappedSpaceRatio(context.freeCells, reachableCells);
        double distanciaRealCola = normalizeDistance(newTailDistance, context.maxDistance);
        double progresoRealCola = normalizeProgress(context.oldTailDistance, newTailDistance, context.maxDistance);
        double riesgoEncierro = calculateTrapRisk(reachableCells, context.snakeLength);
        double comidaSegura = comidaAlcanzable == 1.0 && colaAlcanzable == 1.0 ? 1.0 : 0.0;
        double comeManzana = context.hasApple() && newHeadId == context.appleId ? 1.0 : 0.0;

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

    private static int distanceToNearestWall(int row, int col, int rows, int cols) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return 0;
        }

        int up = row;
        int down = rows - 1 - row;
        int left = col;
        int right = cols - 1 - col;

        return Math.min(Math.min(up, down), Math.min(left, right));
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

    public static final class DecisionContext {
        private final Cell[][] board;
        private final Direction currentDirection;
        private final int rows;
        private final int cols;
        private final int totalCells;
        private final int maxDistance;
        private final int snakeLength;

        private final int headId;
        private final int appleId;
        private final int tailId;
        private final int freeCells;
        private final int[] bodyAndHeadIds;
        private final int bodyAndHeadCount;
        private final int oldFoodDistance;
        private final int oldTailDistance;

        private DecisionContext(Cell[][] board, Direction currentDirection, int score) {
            this.board = board;
            this.currentDirection = currentDirection;
            this.rows = board.length;
            this.cols = board[0].length;
            this.totalCells = rows * cols;
            this.maxDistance = rows + cols;
            this.snakeLength = score;

            int detectedHeadId = -1;
            int detectedAppleId = -1;
            int detectedTailId = -1;
            int detectedFreeCells = 0;
            int[] detectedBodyAndHeadIds = new int[totalCells];
            int detectedBodyAndHeadCount = 0;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int id = idOf(row, col);
                    Cell cell = board[row][col];

                    if (cell == Cell.SNAKE_HEAD) {
                        detectedHeadId = id;
                        detectedBodyAndHeadIds[detectedBodyAndHeadCount++] = id;
                    } else if (cell == Cell.SNAKE_BODY) {
                        detectedBodyAndHeadIds[detectedBodyAndHeadCount++] = id;
                    } else if (cell == Cell.SNAKE_TAIL) {
                        detectedTailId = id;
                        detectedFreeCells++;
                    } else if (cell == Cell.APPLE) {
                        detectedAppleId = id;
                        detectedFreeCells++;
                    } else if (cell == Cell.EMPTY) {
                        detectedFreeCells++;
                    }
                }
            }

            this.headId = detectedHeadId;
            this.appleId = detectedAppleId;
            this.tailId = detectedTailId;
            this.freeCells = detectedFreeCells;
            this.bodyAndHeadIds = detectedBodyAndHeadIds;
            this.bodyAndHeadCount = detectedBodyAndHeadCount;

            BfsResult oldSearch = hasHead() ? runBfs(headId) : BfsResult.empty();
            this.oldFoodDistance = oldSearch.distanceToApple;
            this.oldTailDistance = oldSearch.distanceToTail;
        }

        public boolean hasHead() {
            return headId >= 0;
        }

        public boolean isSafeImmediateMove(Direction direction) {
            if (!hasHead()) {
                return false;
            }

            int nextId = nextId(headId, direction);
            if (nextId < 0) {
                return false;
            }

            Cell cell = board[rowOf(nextId)][colOf(nextId)];
            return cell != Cell.SNAKE_HEAD && cell != Cell.SNAKE_BODY;
        }

        private boolean hasApple() {
            return appleId >= 0;
        }

        private boolean hasTail() {
            return tailId >= 0;
        }

        private int idOf(int row, int col) {
            return row * cols + col;
        }

        private int rowOf(int id) {
            if (id < 0) {
                return -1;
            }
            return id / cols;
        }

        private int colOf(int id) {
            if (id < 0) {
                return -1;
            }
            return id % cols;
        }

        private int nextId(int id, Direction direction) {
            if (id < 0) {
                return -1;
            }

            int row = rowOf(id);
            int col = colOf(id);

            return switch (direction) {
                case UP -> row > 0 ? idOf(row - 1, col) : -1;
                case DOWN -> row < rows - 1 ? idOf(row + 1, col) : -1;
                case LEFT -> col > 0 ? idOf(row, col - 1) : -1;
                case RIGHT -> col < cols - 1 ? idOf(row, col + 1) : -1;
            };
        }

        private boolean isPassable(int id) {
            if (id < 0) {
                return false;
            }

            Cell cell = board[rowOf(id)][colOf(id)];
            return cell == Cell.EMPTY || cell == Cell.APPLE || cell == Cell.SNAKE_TAIL;
        }

        private int countLocalFreedom(int id) {
            int count = 0;

            for (Direction direction : DIRECTIONS) {
                if (isPassable(nextId(id, direction))) {
                    count++;
                }
            }

            return count;
        }

        private int distanceToNearestBody(int id) {
            if (id < 0) {
                return 0;
            }

            int row = rowOf(id);
            int col = colOf(id);
            int best = maxDistance;

            for (int i = 0; i < bodyAndHeadCount; i++) {
                int bodyId = bodyAndHeadIds[i];
                int distance = Math.abs(row - rowOf(bodyId)) + Math.abs(col - colOf(bodyId));

                if (distance > 0 && distance < best) {
                    best = distance;
                }
            }

            return best;
        }

        private int foodImprovement(int newHeadId) {
            if (!hasApple() || newHeadId < 0) {
                return 0;
            }

            return manhattan(headId, appleId) - manhattan(newHeadId, appleId);
        }

        private boolean isFoodInFront(int newHeadId, Direction direction) {
            if (!hasApple() || newHeadId < 0) {
                return false;
            }

            int newRow = rowOf(newHeadId);
            int newCol = colOf(newHeadId);
            int appleRow = rowOf(appleId);
            int appleCol = colOf(appleId);

            return switch (direction) {
                case UP -> appleCol == newCol && appleRow < newRow;
                case DOWN -> appleCol == newCol && appleRow > newRow;
                case LEFT -> appleRow == newRow && appleCol < newCol;
                case RIGHT -> appleRow == newRow && appleCol > newCol;
            };
        }

        private int manhattan(int firstId, int secondId) {
            if (firstId < 0 || secondId < 0) {
                return 0;
            }

            return Math.abs(rowOf(firstId) - rowOf(secondId))
                    + Math.abs(colOf(firstId) - colOf(secondId));
        }

        private BfsResult runBfs(int startId) {
            if (startId < 0) {
                return BfsResult.empty();
            }

            boolean[] visited = new boolean[totalCells];
            int[] distances = new int[totalCells];
            int[] queue = new int[totalCells];
            Arrays.fill(distances, -1);

            int head = 0;
            int tail = 0;
            visited[startId] = true;
            distances[startId] = 0;
            queue[tail++] = startId;

            int reachableCells = 0;
            int distanceToApple = startId == appleId ? 0 : -1;
            int distanceToTail = startId == tailId ? 0 : -1;

            while (head < tail) {
                int currentId = queue[head++];
                reachableCells++;

                for (Direction direction : DIRECTIONS) {
                    int nextId = nextId(currentId, direction);

                    if (nextId < 0 || visited[nextId] || !isPassable(nextId)) {
                        continue;
                    }

                    visited[nextId] = true;
                    distances[nextId] = distances[currentId] + 1;
                    queue[tail++] = nextId;

                    if (nextId == appleId) {
                        distanceToApple = distances[nextId];
                    }

                    if (nextId == tailId) {
                        distanceToTail = distances[nextId];
                    }
                }
            }

            return new BfsResult(reachableCells, distanceToApple, distanceToTail);
        }
    }

    private static final class BfsResult {
        private final int reachableCells;
        private final int distanceToApple;
        private final int distanceToTail;

        private BfsResult(int reachableCells, int distanceToApple, int distanceToTail) {
            this.reachableCells = reachableCells;
            this.distanceToApple = distanceToApple;
            this.distanceToTail = distanceToTail;
        }

        private static BfsResult empty() {
            return new BfsResult(0, -1, -1);
        }
    }
}
