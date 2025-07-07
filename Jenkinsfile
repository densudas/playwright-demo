pipeline {
    agent {
        docker {
            image 'openjdk:21-jdk'
        }
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        
        stage('Build') {
            steps {
                sh './gradlew build -x test'
            }
        }
        
        stage('Install Playwright Browsers') {
            steps {
                sh './gradlew installPlaywrightBrowsers'
            }
        }
        
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'build/reports/**, build/test-results/**, *.png, trace.zip', allowEmptyArchive: true
        }
    }
    
    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
    }
}
