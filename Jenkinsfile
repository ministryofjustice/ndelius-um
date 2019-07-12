pipeline {
    agent { label "jenkins_slave" }
    options {
        disableConcurrentBuilds()
    }
    triggers {
        cron('H */8 * * *')
        pollSCM('* * * * *')
    }
    parameters {
        string(defaultValue: "SNAPSHOT", name: 'version')
        string(defaultValue: "SNAPSHOT", name: 'nextVersion')
    }
    stages {
        stage('Init') {
            steps {
                slackSend(message: "Build started  - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace(':8080','')}|Open>)")
            }
        }
        stage('Build') {
            when {
                expression { params.version == 'SNAPSHOT' }
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sh './gradlew clean build'
                }
            }
        }
        stage('Release') {
            when {
                expression { params.version != 'SNAPSHOT' }
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sh './gradlew clean release -Prelease.releaseVersion=$version -Prelease.newVersion=$nextVersion -Prelease.useAutomaticVersion=true'
                }
            }
        }
        stage('Push') {
            when { branch 'master' }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sh 'docker build -t 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:latest -t 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:$version --no-cache .'
                    sh 'aws ecr get-login --no-include-email --region eu-west-2 | source /dev/stdin'
                    sh 'docker push 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:$version'
                    sh 'docker push 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:latest'
                }
            }
        }
    }
    post {
        always {
            junit 'build/test-results/**/*.xml'
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
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
