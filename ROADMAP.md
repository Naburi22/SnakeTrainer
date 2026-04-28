# Snake Trainer - Roadmap de Computación Evolutiva

## 1. Objetivo

Desarrollar un sistema de entrenamiento para el juego Snake basado en computación evolutiva, donde los agentes aprendan a maximizar su rendimiento mediante un vector de pesos.

---

## 2. Arquitectura del proyecto

```
snaketrainer/
 ├── model/
 ├── game/
 ├── agent/
 ├── analysis/
 ├── evolution/
 │   ├── evaluation/
 │   ├── selection/
 │   └── reproduction/
 └── ui/
```

### Responsabilidades

* **model/**: estructuras básicas (Cell, Direction, Position)
* **game/**: lógica del Snake
* **agent/**: definición y comportamiento del agente
* **analysis/**: análisis del tablero y extracción de features
* **evolution/**: algoritmo evolutivo completo
* **ui/**: visualización

---

## 3. Agente

### Concepto

Cada agente contiene un vector de pesos:

```
W = [w1, w2, w3, ..., wn]
```

Cada peso representa la importancia de una característica del estado.

### Evaluación de movimientos

Para cada movimiento posible:

```
valor = Σ (wi * fi)
```

Donde:

* `wi`: peso
* `fi`: feature calculada del tablero

Se ejecuta el movimiento con mayor valor.

### Separación de responsabilidades

El agente **NO debe calcular directamente las features**.

Debe delegar en:

* `FeatureExtractor`
* `BoardAnalyzer`

---

## 4. Features recomendadas

Ejemplos iniciales:

* Distancia a la manzana
* Movimiento seguro (no muerte inmediata)
* Espacio libre accesible
* Cola accesible (clave)
* Cercanía a paredes

---

## 5. Evaluación (Fitness)

### Opción 1: Solo manzanas

```
fitness = apples
```

**Ventajas**:

* Directo al objetivo
* No favorece comportamiento pasivo

**Desventajas**:

* Poca diferenciación al inicio

---

### Opción 2: Manzanas + tiempo

```
fitness = apples * 1000 + steps
```

**Ventajas**:

* Mejora aprendizaje inicial
* Penaliza muertes rápidas

**Desventajas**:

* Puede favorecer supervivencia pasiva

---

### Recomendación

```
fitness = apples * 1000 + steps
```

---

## 6. Elitismo

El mejor agente pasa directamente a la siguiente generación.

Recomendación:

```
eliteCount = 1
```

---

## 7. Selección

### Método recomendado: Torneo

Proceso:

1. Seleccionar k agentes aleatorios
2. Elegir el mejor

Recomendación:

```
k = 3
```

---

### Alternativa: Ruleta

```
P(i) = fitness_i / Σ fitness
```

**Problemas**:

* Sensible a valores extremos
* Problemas si fitness = 0

---

## 8. Reproducción

Pendiente de definir, pero opciones típicas:

### Crossover

* Uniforme
* Un punto

### Mutación

* Pequeñas variaciones aleatorias en pesos

Ejemplo:

```
wi = wi + random(-ε, ε)
```

---

## 9. Flujo del algoritmo

```
1. Inicializar población
2. Para cada generación:
   2.1 Simular cada agente
   2.2 Calcular fitness
   2.3 Guardar mejor agente
   2.4 Aplicar elitismo
   2.5 Seleccionar padres
   2.6 Cruzar
   2.7 Mutar
   2.8 Generar nueva población
3. Ejecutar mejor agente final
```

---

## 10. Análisis del tablero

Nueva capa clave:

```
analysis/
```

### Componentes

* `BoardAnalyzer`
* `FeatureExtractor`
* `MoveSimulation`

---

## 11. Feature crítica: Cola accesible

### Objetivo

Determinar si, tras un movimiento, existe camino hasta la cola.

### Algoritmo

1. Simular movimiento
2. Obtener nueva cabeza
3. Obtener cola
4. Ejecutar BFS/DFS
5. Comprobar si existe camino

### Transitabilidad

Permitido:

* EMPTY
* APPLE
* SNAKE_TAIL

Bloqueado:

* SNAKE_BODY
* SNAKE_HEAD

---

## 12. Componentes evolutivos

### evolution/

* `EvolutionEngine`: orquestador
* `Population`: conjunto de agentes
* `Individual`: agente + fitness

### evaluation/

* `FitnessEvaluator`

### selection/

* `SelectionStrategy`
* `TournamentSelection`
* `RouletteSelection`

### reproduction/

* `CrossoverStrategy`
* `MutationStrategy`
* `ReproductionEngine`

---

## 13. Principios clave de diseño

* Separar lógica de juego y evolución
* Agente = solo decisión
* Features = módulo independiente
* Motor evolutivo modular
* Evitar clases monolíticas

---

## 14. Siguientes pasos

1. Implementar `WeightedAgent`
2. Crear `FeatureExtractor`
3. Implementar `FitnessEvaluator`
4. Implementar `TournamentSelection`
5. Implementar `MutationStrategy`
6. Integrar todo en `EvolutionEngine`
7. Sustituir `GreedyAgent`

---

## 15. Estado actual del proyecto

✔ Motor Snake completo
✔ Representación matricial
✔ UI funcional
✔ Agente básico (Greedy)

Pendiente:

* Sistema evolutivo completo
* Features avanzadas
* Optimización de fitness
