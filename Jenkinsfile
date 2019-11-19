pipeline {
    agent { label "jenkins_slave" }
    options {
        disableConcurrentBuilds()
    }
    triggers {
        cron('H */8 * * *')
        pollSCM('H/15 * * * *')
    }
    parameters {
        string(defaultValue: "latest", name: 'version', description: 'eg. 1.2.3, or latest for a snapshot build')
        string(defaultValue: "latest", name: 'nextVersion', description: 'eg. 1.2.4-SNAPSHOT, or latest for a snapshot build')
    }
    stages {
        stage('Init') {
            steps {
                slackSend(message: "Build started  - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace(':8080','')}|Open>)")
            }
        }
        stage('Build') {
            when { expression { params.version == 'latest' } }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sh './gradlew clean build'
                }
            }
        }
        stage('Release') {
            when { expression { params.version != 'latest' } }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    deleteDir()
                    git url: 'git@github.com:ministryofjustice/ndelius-um', branch: 'master', credentialsId: 'f44bc5f1-30bd-4ab9-ad61-cc32caf1562a'
                    sshagent(credentials: ['f44bc5f1-30bd-4ab9-ad61-cc32caf1562a']) {
                        sh './gradlew clean release -Prelease.releaseVersion=$version -Prelease.newVersion=$nextVersion -Prelease.useAutomaticVersion=true'
                    }
                }
            }
        }
        stage('Push') {
            when { branch 'release-job' }
            environment {
                snapshotVersion = sh (script: 'source ./gradle.properties && echo "${version}"', returnStdout: true).trim()
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sh '''
                        echo "Pushing ${version}..."
                        docker build -t 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:latest \
                                     -t 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:$version \
                                     -t 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:$snapshotVersion \
                                     --no-cache .
                        aws ecr get-login --no-include-email --region eu-west-2 | source /dev/stdin
                        docker push 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:latest
                        docker push 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:$version
                        docker push 895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um:$snapshotVersion
                    '''
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
