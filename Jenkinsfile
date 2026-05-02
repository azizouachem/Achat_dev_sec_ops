pipeline {
    agent any

    // ── Tool aliases configured in Jenkins → Global Tool Configuration ──
    tools {
        maven 'Maven-3'
        jdk   'JDK-8'
    }

    environment {
        // Nexus
        NEXUS_VERSION    = 'nexus3'
        NEXUS_PROTOCOL   = 'http'
        NEXUS_URL        = 'nexus:8081'          // container name – reachable inside Docker network
        NEXUS_REPO       = 'maven-releases'    // Nexus built-in repo (no manual creation needed)
        NEXUS_CREDENTIAL = 'nexus-credentials'    // Jenkins credential ID

        // SonarQube
        SONAR_SERVER     = 'SonarQube'            // Jenkins SonarQube server name

        // Docker
        DOCKER_IMAGE     = 'achat-app'
        DOCKER_TAG       = "${env.BUILD_NUMBER}"
        DOCKER_REGISTRY  = 'localhost:5000'       // local registry – update for DockerHub
    }

    stages {

        // ── 1. Checkout ──────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo '📥 Cloning repository from Git...'
                checkout scm
            }
        }

        // ── 2. Build ─────────────────────────────────────────────────────
        stage('Build') {
            steps {
                echo '🔨 Building project with Maven (skip tests here, run separately)...'
                sh 'mvn clean package -DskipTests'
            }
            post {
                success { echo '✅ Build succeeded.' }
                failure { echo '❌ Build failed.' }
            }
        }

        // ── 3. Unit Tests ─────────────────────────────────────────────────
        stage('Unit Tests') {
            steps {
                echo '🧪 Running JUnit tests with JaCoCo coverage...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit 'target/surefire-reports/*.xml'
                    // Publish JaCoCo coverage report
                    jacoco(
                        execPattern:         'target/jacoco.exec',
                        classPattern:        'target/classes',
                        sourcePattern:       'src/main/java',
                        exclusionPattern:    '**/*Test*,**/entities/**,**/repositories/**'
                    )
                }
                failure { echo '❌ Tests failed.' }
            }
        }

        // ── 4. SonarQube Analysis ─────────────────────────────────────────
        stage('Code Quality – SonarQube') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withSonarQubeEnv("${SONAR_SERVER}") {
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=achat \
                          -Dsonar.projectName="Achat Application" \
                          -Dsonar.projectVersion=1.0 \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        // ── 5. Quality Gate ───────────────────────────────────────────────
        stage('Quality Gate') {
            steps {
                echo '🚦 Waiting for SonarQube Quality Gate result...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ── 6. Publish to Nexus ───────────────────────────────────────────
        stage('Publish Artifact – Nexus') {
            steps {
                echo '📦 Publishing JAR to Nexus...'
                script {
                    def pom       = readMavenPom file: 'pom.xml'
                    def artifactPath = "target/${pom.artifactId}-${pom.version}.jar"

                    nexusArtifactUploader(
                        nexusVersion:  env.NEXUS_VERSION,
                        protocol:      env.NEXUS_PROTOCOL,
                        nexusUrl:      env.NEXUS_URL,
                        groupId:       pom.groupId,
                        version:       pom.version,
                        repository:    env.NEXUS_REPO,
                        credentialsId: env.NEXUS_CREDENTIAL,
                        artifacts: [[
                            artifactId: pom.artifactId,
                            classifier: '',
                            file:       artifactPath,
                            type:       'jar'
                        ],[
                            artifactId: pom.artifactId,
                            classifier: '',
                            file:       'pom.xml',
                            type:       'pom'
                        ]]
                    )
                }
            }
        }

        // ── 7. Docker Build ───────────────────────────────────────────────
        stage('Docker Build') {
            steps {
                echo "🐳 Building Docker image ${DOCKER_IMAGE}:${DOCKER_TAG}..."
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }

        // ── 8. Docker Push ────────────────────────────────────────────────
        stage('Docker Push') {
            steps {
                echo '📤 Pushing Docker image to registry...'
                withCredentials([usernamePassword(
                    credentialsId: 'docker-registry-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh "docker login ${DOCKER_REGISTRY} -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                    sh "docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:latest"
                }
            }
        }

        // ── 9. Deploy (docker-compose) ────────────────────────────────────
        stage('Deploy – Docker Compose') {
            steps {
                echo '🚀 Deploying application stack with docker-compose...'
                sh 'docker-compose down --remove-orphans || true'
                sh "DOCKER_TAG=${DOCKER_TAG} docker-compose up -d --build"
            }
        }
    }

    // ── Post-pipeline notifications ───────────────────────────────────────
    post {
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
        }
        success {
            echo '🎉 Pipeline completed successfully!'
            mail(
                to:      'team@example.com',
                subject: "✅ [Jenkins] Build #${env.BUILD_NUMBER} – SUCCESS – ${env.JOB_NAME}",
                body:    "Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER} succeeded.\n\nView: ${env.BUILD_URL}"
            )
        }
        failure {
            echo '🚨 Pipeline failed!'
            mail(
                to:      'team@example.com',
                subject: "❌ [Jenkins] Build #${env.BUILD_NUMBER} – FAILED – ${env.JOB_NAME}",
                body:    "Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER} FAILED.\n\nView: ${env.BUILD_URL}"
            )
        }
    }
}
