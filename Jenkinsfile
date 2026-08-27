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

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    bat '''
                        docker login -u "%DOCKER_USERNAME%" -p "%DOCKER_PASSWORD%"
                        docker tag employee-management-system:latest %DOCKER_USERNAME%/employee-management-system:latest
                        docker push %DOCKER_USERNAME%/employee-management-system:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                bat '''
                    docker stop ems-container >nul 2>&1
                    docker rm ems-container >nul 2>&1
                    docker run -d --name ems-container -p 8090:8090 -e DB_HOST=host.docker.internal -e DB_PORT=3306 -e DB_USERNAME=root -e DB_PASSWORD=root employee-management-system:latest
                '''
            }
        }
    }
}