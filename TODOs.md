# TODOs Snake Trainer

## Reunión 06/05

- [ ] Explorar nuevos parámetros.
- [ ] Explorar activar y desactivar ciertos parámetros.
- [ ] Cruce de genes activados o desactivados condicionado por el ranking.
- [ ] Discretizar parámetros.
- [ ] Favorecer distintos parámetros según fase de la partida (favorecer eficiencia al principio y aprovechar el espacio al final).
- [x] Crecimiento del límite de pasos durante entrenamiento conforme avanza la partida.
- [ ] Ajustar los límites.

## Ideas del cuarto integrante

- [ ] Metaevaluación de parámetros.
- [ ] Ajuste dinámico de mutación.
- [ ] Nuevas features.
- [ ] Mejor evaluación.
- [ ] Visualización avanzada.
- [ ] Persistencia de agentes.

## Ideas de Álvaro ©

- [x] Animación durante entrenamiento para que no parezca que está congelado.
- [x] Enseñar durante entrenamiento la generación que se está entrenando, mostrar más información relevante.
- [x] Hacer el entrenamiento concurrente.

## Para la memoria

- [ ] Explicacion exacta del proceso de seleccion y reproduccion.
- [ ] Explicacion exacta del proceso de construccion del vector de pesos, y de la activacion o no activacion de los parametros.

- [ ] Añadir probabilidad de cruce (no asegurar), repetir cruces hasta llenar poblacion.
- [ ] Cambiar cantidad de mutacion de [-0.2, 0.2] a un intervalo con un porcentaje del peso [-10%, +10%].
- [ ] El constructor repara el genoma si tiene demasiadas o muy pocas features activas.
- [ ] Modificar operador alfa para que favorezca un poco al progenitor superior (introduce sesgo?).
- [ ] Cambiar las probabilidades de heredar la activacion de un gen a: 70% del padre superior y 30% del padre inferior (tanto valor del peso como de su activacion).

- [ ] Definir función de aprendizaje (La función de aprendizaje ajusta los parámetros del algoritmo (como las tasas de mutación) para mejorar la búsqueda con el tiempo, optimizando cómo evoluciona el sistema).

