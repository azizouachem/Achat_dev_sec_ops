pipeline {
    agent any

    // ── Tool aliases configured in Jenkins → Global Tool Configuration ──
    tools {
        maven 'Maven-3'
        jdk   'JDK-21'
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
        stage('Initialize') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USER')]) {
                    script {
                        // This makes the password available for the whole pipeline session
                        env.NEXUS_PASSWORD = NEXUS_PASSWORD
                    }
                }
            }
        }

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
                echo '🔨 Building project...'
                sh 'mvn -s settings.xml clean package -DskipTests'
            }
        }

        // ── 3. Unit Tests ─────────────────────────────────────────────────
        stage('Unit Tests') {
            steps {
                echo '🧪 Running JUnit tests...'
                sh 'mvn -s settings.xml test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: 'target/jacoco.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java'
                    )
                }
            }
        }

        // ── 4. SonarQube Analysis ─────────────────────────────────────────
        stage('Code Quality – SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn -s settings.xml sonar:sonar'
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

    // ── Post-pipeline cleanup ─────────────────────────────────────────────
    post {
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
        }
        success {
            echo '🎉 Pipeline completed successfully!'
        }
        failure {
            echo '🚨 Pipeline failed!'
        }
    }
}
