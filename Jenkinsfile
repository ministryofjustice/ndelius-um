def deploy(account_id, cluster_name, service_name) {
    wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
        sh '''
            set +x
            echo "Assuming role in account ${account_id}..."
            creds=`aws sts assume-role --role-arn arn:aws:iam::${account_id}:role/terraform --role-session-name deploy-usermanagement-\$RANDOM`
            export AWS_ACCESS_KEY_ID="`echo \$creds | jq -r '.Credentials.AccessKeyId'`"
            export AWS_SECRET_ACCESS_KEY="`echo \$creds | jq -r '.Credentials.SecretAccessKey'`"
            export AWS_SESSION_TOKEN="`echo \$creds | jq -r '.Credentials.SessionToken'`"
            aws sts get-caller-identity
            echo "Starting deployment..."
            aws ecs update-service --region eu-west-2 --cluster del-delius-ecscluster-private-ecs --service del-test-usermanagement-service --force-new-deployment
            unset AWS_ACCESS_KEY_ID
            unset AWS_SECRET_ACCESS_KEY
            unset AWS_SESSION_TOKEN
            set -x
        '''
    }
}

pipeline {
    agent { label "jenkins_slave" }
    options {
        disableConcurrentBuilds()
    }
    triggers {
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
                    sh './gradlew clean build --info'
                }
            }
        }
        stage('Release') {
            when { expression { params.version != 'latest' } }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sshagent(credentials: ['f44bc5f1-30bd-4ab9-ad61-cc32caf1562a']) {
                        sh './gradlew clean release -Prelease.releaseVersion=$version -Prelease.newVersion=$nextVersion -Prelease.useAutomaticVersion=true'
                    }
                }
            }
        }
        stage('Push') {
            when { branch 'master' }
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
        stage('Deploy to Dev') {
            when { branch 'master' }
            steps {
                deploy('723123699647', 'dlc-delius-ecscluster-private-ecs', 'dlc-dev-usermanagement-service')
            }
        }
        stage('Deploy to Test') {
            when { branch 'master' }
            steps {
                deploy('728765553488', 'del-delius-ecscluster-private-ecs', 'del-test-usermanagement-service')
            }
        }
    }
    post {
        always {
            junit 'build/test-results/**/*.xml'
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            publishHTML target: [reportName : 'API Test Report', reportDir: 'build/reports/tests/test', reportFiles: 'index.html', allowMissing: true, keepAll: true]
            publishHTML target: [reportName : 'API Coverage Report', reportDir: 'build/reports/coverage/test', reportFiles: 'index.html', allowMissing: true, keepAll: true]
            publishHTML target: [reportName : 'UI Test Report', reportDir: 'build/reports/tests/ui-test', reportFiles: 'index.html', allowMissing: true, keepAll: true]
            publishHTML target: [reportName : 'UI Coverage Report', reportDir: 'build/reports/coverage/ui-test', reportFiles: 'index.html', allowMissing: true, keepAll: true]
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
