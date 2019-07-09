pipeline {
    agent { label "jenkins_slave" }
    triggers {
        cron('H */8 * * *')
        pollSCM('* * * * *')
    }
    stages {
        stage('Init') {
            steps {
                slackSend(message: "Build started  - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace(':8080','')}|Open>)")
            }
        }
        stage('Assemble') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    script {
                        try {
                            sh './gradlew clean build'
                        } finally {
                            junit '**/build/test-results/**/*.xml'
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
        success {
            slackSend(message: "Build completed - ${env.JOB_NAME} ${env.BUILD_NUMBER} ", color: 'good')
        }
        failure {
            slackSend(message: "Build failed - ${env.JOB_NAME} ${env.BUILD_NUMBER} ", color: 'danger')
        }
    }
}
