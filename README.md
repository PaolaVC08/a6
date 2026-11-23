# Programa 6 - Buscar x para que la integral de la t de 0 a x sea p

Dado `p` y los grados de libertad `dof`, encuentra `x` con el algoritmo del kit
(x inicial = 1, d = 0.5; si la integral es baja suma d, si es alta resta d,
y divide d entre 2 cada vez que cambia el signo del error).
La integral usa la regla de Simpson del Programa 5.

## Ejecutar sin Maven

    cd src/main/java
    javac *.java
    java App 0.2 6        # p dof
    java App              # los 3 casos de la Tabla 1

## Pruebas y cobertura (Maven)

    mvn clean verify
    # reporte de cobertura: target/site/jacoco/index.html

## Jenkins

El archivo `Jenkinsfile` define el pipeline: version de herramientas,
`mvn clean verify` y ejecucion de la Tabla 1.

## Clases

| Clase | Que hace |
|---|---|
| `App` | Lee `p` y `dof`, ejecuta la busqueda y guarda `resultados.txt` |
| `SearchX` | Algoritmo de busqueda de x (el nuevo del Programa 6) |
| `Logic` | Integral de 0 a x con Simpson duplicando segmentos (Programa 5) |
| `SimpsonIntegration` | Regla de Simpson paso a paso (Programa 5) |
| `GammaFunction` | Funcion Gamma para enteros y semienteros (Programa 5) |
| `Output` | Escribe archivos |
