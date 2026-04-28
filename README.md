# Snake Trainer - Proyecto base Java

Proyecto base para un Snake pensado para entrenar agentes mediante computación evolutiva.

## Características

La matriz del tablero distingue explícitamente entre:

- `SNAKE_HEAD`: cabeza de la serpiente
- `SNAKE_BODY`: cuerpo intermedio
- `SNAKE_TAIL`: cola
- `APPLE`: manzana
- `EMPTY`: casilla vacía

Esto permite que la IA evalúe parámetros más complejos, por ejemplo si después de ejecutar un movimiento la cola sigue siendo accesible mediante algún camino.

El método principal está en:

```java
public Cell[][] getBoardMatrix()
  ```

- También existe una versión numérica:

  ```java
  int[][] board = game.getNumericBoardMatrix();
  ```

  Codificación:
    - 0: vacío
    - 1: cabeza de la serpiente
    - 2: cuerpo intermedio de la serpiente
    - 3: cola de la serpiente
    - 4: manzana

- Interfaz gráfica Swing.
- Tablero a la izquierda.
- Información de la partida a la derecha.
- Inputs:
  - número de generaciones,
  - número de agentes por generación,
  - botón ejecutar.

## Estructura

```text
SnakeTrainerComplete/
 ├── README.md
 └── src/
     └── main/
         └── java/
             └── snaketrainer/
                 ├── Main.java
                 ├── model/
                 │   ├── Cell.java
                 │   ├── Direction.java
                 │   └── Position.java
                 ├── game/
                 │   └── SnakeGame.java
                 ├── agent/
                 │   ├── SnakeAgent.java
                 │   └── GreedyAgent.java
                 ├── trainer/
                 │   ├── SnakeTrainer.java
                 │   └── TrainingResult.java
                 └── ui/
                     ├── SnakeBoardPanel.java
                     └── SnakeWindow.java
```

## Compilar desde terminal

Desde la raíz del proyecto:

```bash
javac -d out src/main/java/snaketrainer/Main.java src/main/java/snaketrainer/model/*.java src/main/java/snaketrainer/game/*.java src/main/java/snaketrainer/agent/*.java src/main/java/snaketrainer/trainer/*.java src/main/java/snaketrainer/ui/*.java
```

## Ejecutar

```bash
java -cp out snaketrainer.Main
```

## Dónde conectar la IA

La interfaz que debe implementar cualquier agente está en:

```text
src/main/java/snaketrainer/agent/SnakeAgent.java
```

Método principal:

```java
Direction decideMove(Cell[][] board, Direction currentDirection, int score);
```

En el futuro puedes crear, por ejemplo:

```text
EvolutionaryAgent.java
```

e implementar ahí el vector de pesos.

## Dónde conectar el algoritmo evolutivo

La clase preparada para sustituir la lógica provisional está en:

```text
src/main/java/snaketrainer/trainer/SnakeTrainer.java
```

Ahora mismo usa un `GreedyAgent` provisional. Esa parte está marcada con comentarios para reemplazarla por:

- evaluación de población,
- selección,
- cruce,
- mutación,
- creación de nuevas generaciones.
