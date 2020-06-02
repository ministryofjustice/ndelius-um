image = '895523100917.dkr.ecr.eu-west-2.amazonaws.com/hmpps/ndelius-um'

def deploy(account_id, cluster_name, service_name) {
    wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
        sh """
            set +x
            echo "Assuming role in account ${account_id}..."
            creds=`aws sts assume-role --role-arn arn:aws:iam::${account_id}:role/terraform --role-session-name deploy-usermanagement-\$RANDOM`
            export AWS_ACCESS_KEY_ID="`echo \$creds | jq -r '.Credentials.AccessKeyId'`"
            export AWS_SECRET_ACCESS_KEY="`echo \$creds | jq -r '.Credentials.SecretAccessKey'`"
            export AWS_SESSION_TOKEN="`echo \$creds | jq -r '.Credentials.SessionToken'`"
            aws sts get-caller-identity
            echo "Starting deployment..."
            aws ecs update-service --region eu-west-2 --cluster ${cluster_name} --service ${service_name} --force-new-deployment
            unset AWS_ACCESS_KEY_ID
            unset AWS_SECRET_ACCESS_KEY
            unset AWS_SESSION_TOKEN
            set -x
        """
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
            environment {
                BRANCH = env.BRANCH_NAME.replace('/', '_')
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sh """
                        [ "\${BRANCH}" != "master" ] && sed -i "s/-SNAPSHOT/-SNAPSHOT.\${BRANCH}/" gradle.properties
                        source ./gradle.properties
                        ./gradlew clean build bootBuildImage --info --build-cache

                        set +x
                        docker tag "delius-user-management:\${version}" "${image}:\${version}"
                        docker tag "${image}:\${version}" "${image}:latest"
                        aws ecr get-login --no-include-email --region eu-west-2 | source /dev/stdin
                        docker push "${image}:\${version}"
                        [ "\${BRANCH}" == "master" ] && docker push "${image}:latest"
                    """
                }
            }
        }
        stage('Release') {
            when { expression { params.version != 'latest' } }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    sshagent(credentials: ['f44bc5f1-30bd-4ab9-ad61-cc32caf1562a']) {
                        sh """
                            ./gradlew clean release -Prelease.releaseVersion=\${version} -Prelease.newVersion=\${nextVersion} -Prelease.useAutomaticVersion=true --info

                            set +x
                            docker tag "delius-user-management:\${version}" "${image}:\${version}"
                            docker tag "${image}:\${version}" "${image}:latest"
                            aws ecr get-login --no-include-email --region eu-west-2 | source /dev/stdin
                            docker push "${image}:\${version}"
                            docker push "${image}:latest"
                        """
                    }
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
