pipeline {
    agent any

    tools {
        // Names must match the tools configured under Manage Jenkins > Tools
        jdk   'jdk17'
        maven 'maven3'
    }

    options {
        timestamps()
        skipDefaultCheckout(true)            // we do the checkout explicitly below
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        GIT_URL    = 'https://github.com/your-org/ci-demo.git'
        GIT_BRANCH = 'main'
        GIT_CREDS  = 'git-credentials-id'    // Jenkins credentials ID (omit for public repos)
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
    }

    stages {

        stage('Git Checkout') {
            steps {
                checkout scmGit(
                    branches: [[name: "*/${env.GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: env.GIT_URL, credentialsId: env.GIT_CREDS]]
                )
                script {
                    env.GIT_SHORT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
                echo "Checked out ${env.GIT_BRANCH} @ ${env.GIT_SHORT_SHA}"
            }
        }

        stage('Code Coverage Test') {
            steps {
                // Runs unit tests with the JaCoCo agent, generates the report,
                // and fails the build if coverage drops below the threshold in pom.xml
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    // Requires the "Coverage" plugin (successor of the JaCoCo plugin)
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']],
                        qualityGates: [[threshold: 80.0, metric: 'LINE', baseline: 'PROJECT']]
                    )
                    publishHTML(target: [
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report',
                        keepAll: true
                    ])
                }
            }
        }

        stage('Build') {
            steps {
                // Tests already ran in the previous stage; just package the artifact
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success { echo "Pipeline succeeded for ${env.GIT_SHORT_SHA}" }
        failure { echo "Pipeline FAILED for ${env.GIT_SHORT_SHA}" }
        always  { cleanWs(deleteDirs: true, patterns: [[pattern: '.m2/**', type: 'EXCLUDE']]) }
    }
}
