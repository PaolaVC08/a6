// Pipeline de Jenkins para el Programa 6.
// Hace lo mismo que harias a mano: mvn clean verify, y despues corre la Tabla 1.
pipeline {
    agent any

    // Si Jenkins no encuentra java 21 o mvn, configuralos en
    // "Administrar Jenkins > Tools" con estos nombres y descomenta el bloque:
    // tools {
    //     jdk 'JDK21'
    //     maven 'Maven3'
    // }

    triggers {
        // Revisa el repositorio cada ~2 minutos y construye si hay cambios nuevos
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Version de herramientas') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Compilar y probar') {
            steps {
                sh 'mvn --batch-mode clean verify'
            }
        }

        stage('Ejecutar casos de la Tabla 1') {
            steps {
                sh 'java -cp target/classes App'
            }
        }
    }

    post {
        always {
            // Muestra cada prueba (pasa/falla) en la pagina del build
            junit 'target/surefire-reports/*.xml'
            // Guarda el reporte de cobertura y el resultado del programa
            archiveArtifacts artifacts: 'target/site/jacoco/**, resultados.txt',
                             allowEmptyArchive: true
        }
    }
}
