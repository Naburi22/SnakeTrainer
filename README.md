# Snake Trainer - Computación Evolutiva

Proyecto en Java que implementa el juego clásico Snake junto con un sistema de entrenamiento de agentes mediante algoritmos evolutivos.

## Descripción general

- Simulación del juego Snake
- Entrenamiento evolutivo de agentes
- Visualización del mejor agente
- Prueba manual de agentes

## Estructura del proyecto

El proyecto sigue una organización modular para separar claramente la lógica del juego, la IA y la interfaz gráfica:

```text
SnakeTrainerComplete/
├── README.md
├── .gitignore
├── logs/
│ └── .gitkeep
│
└── src/
└── main/
└── java/
└── snaketrainer/
├── Main.java
│
├── model/
│ ├── Cell.java
│ ├── Direction.java
│ └── Position.java
│
├── game/
│ └── SnakeGame.java
│
├── analysis/
│ └── FeatureExtractor.java
│
├── agent/
│ ├── SnakeAgent.java
│ ├── GreedyAgent.java
│ ├── WeightedAgent.java
│ ├── WeightVector.java
│ ├── FeatureVector.java
│ └── FeatureName.java
│
├── evolution/
│ ├── EvolutionEngine.java
│ ├── EvolutionConfig.java
│ ├── EvolutionLogger.java
│ ├── EvolutionProgressListener.java
│ ├── Individual.java
│ ├── Population.java
│ └── GenerationResult.java
│
├── evolution/
│ ├── evaluation/
│ │ ├── FitnessEvaluator.java
│ │ ├── FitnessResult.java
│ │ └── EndCause.java
│ │
│ ├── selection/
│ │ ├── SelectionStrategy.java
│ │ └── TournamentSelection.java
│ │
│ └── reproduction/
│ ├── ReproductionEngine.java
│ ├── ArithmeticCrossover.java
│ └── UniformMutation.java
│
├── trainer/
│ ├── SnakeTrainer.java
│ └── TrainingResult.java
│
├── ui/
│ ├── SnakeBoardPanel.java
│ ├── SnakeWindow.java
│ └── LoadingAnimation.java
└── logs/
  └── evolution_log.txt
```

---

### Descripción de los módulos

#### 🔹 `model`

Define las estructuras básicas del juego:

- Representación de celdas, direcciones y posiciones

---

#### 🔹 `game`

Contiene la lógica del juego Snake:

- Movimiento
- Colisiones
- Generación de comida
- Estado del tablero

---

#### 🔹 `agent`

Define los agentes que toman decisiones:

- Interfaces base (`SnakeAgent`)
- Implementaciones (heurísticas y evolutivas)
- Representación de pesos y features

---

#### 🔹 `analysis`

Encargado de extraer información del tablero:

- Cálculo de features en cada frame
- Base para la toma de decisiones del agente

---

#### 🔹 `evolution`

Núcleo del algoritmo evolutivo:

- Gestión de poblaciones
- Ejecución de generaciones
- Logging de resultados

---

#### 🔹 `evolution/evaluation`

Evaluación de agentes:

- Simulación de partidas
- Cálculo de fitness
- Determinación de causa de finalización

---

#### 🔹 `evolution/selection`

Selección de individuos:

- Implementación de selección por torneo

---

#### 🔹 `evolution/reproduction`

Generación de nuevas poblaciones:

- Crossover aritmético
- Mutación de pesos

---

#### 🔹 `trainer`

Capa de alto nivel:

- Orquesta el entrenamiento
- Comunica evolución e interfaz
- Devuelve resultados finales

---

#### 🔹 `ui`

Interfaz gráfica:

- Renderizado del tablero
- Visualización de información
- Control de ejecución (modo evolutivo y manual)
- Barra de progreso
- Animaciones de carga

---

### Diseño general

El proyecto sigue una arquitectura por capas:

```text
UI → Trainer → Evolution → Agent → Analysis → Game → Model
```

Esto permite:

- Separación clara de responsabilidades
- Facilidad para añadir nuevas features
- Sustitución de componentes sin afectar al resto

## Funcionamiento

### Modo evolutivo

1. Se configuran generaciones y agentes
2. Cada agente juega una partida
3. Se evalúan resultados
4. Se seleccionan y reproducen
5. Se repite el proceso
6. Se muestra el mejor agente

Se genera un archivo:
evolution_log.txt

### Modo manual

Permite introducir pesos manuales y ejecutar una partida.

## Representación

Tablero como matriz Cell[][]

## Agentes

Vector de pesos W aplicado a features X:

DistanciaPared, DistanciaCuerpo, LibertadLocal, EspacioAccesible, MejoraComida,
ComidaEnFrente, ComidaAlcanzable, SeguirRecto, ColaAlcanzable

Evaluación:
f = W · X

Solo movimientos seguros.

## Algoritmo evolutivo

Evaluación:

- Manzanas
- Pasos
- Causa de fin

Criterio:

1. Más manzanas
2. Menos pasos

Condición de parada:

- Muerte
- limiteSinComer = limiteBase + puntuacion * incrementoPorManzana
  - limiteBase: 200
  - incrementoPorManzana: 40
  - limiteSinComer: 600

Selección:
Torneo

Reproducción:
Crossover aritmético

Mutación:
Uniforme en [-1,1]

Elitismo:
Mejor agente pasa directamente

## Logging

Archivo evolution_log.txt con:

- Generaciones
- Agentes
- Pesos
- Puntuación
- Pasos
- Causa de fin

Incluye mejor agente global

## Interfaz

Izquierda: tablero
Derecha: info + parámetros + pesos

## Ejecución

Compilar:
javac -d out (Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object { $_.FullName })

Ejecutar:
java -cp out snaketrainer.Main
