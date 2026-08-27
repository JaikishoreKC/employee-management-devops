pipeline {

    agent any

    tools {
        maven 'Maven'
        jdk 'JDK'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t employee-management-system:latest .'
            }
        }

        stage('Docker Deploy') {
            steps {
                bat '''
                    docker stop ems-container >nul 2>&1
                    docker rm ems-container >nul 2>&1
                    docker run -d --name ems-container -p 8090:8090 employee-management-system:latest
                '''
            }
        }
    }
}