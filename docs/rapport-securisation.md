# 🛡️ Rapport de Sécurisation DevSecOps & Supervision Continue

Ce document constitue le livrable officiel pour la **Semaine 5 (Supervision continue)** et la **Semaine 6 (Sécurisation DevSecOps)**. Il détaille la configuration de la supervision continue, l'analyse des risques de sécurité applicative et du pipeline CI/CD, les mesures correctives implémentées, ainsi que les recommandations stratégiques alignées sur l'OWASP Top 10.

---

## 📈 Partie 1 : Supervision Continue (Semaine 5)

La supervision a été mise en place pour collecter, stocker et visualiser les indicateurs de performance (KPI) de l'application et de l'infrastructure en temps réel.

### 1. Architecture de Supervision
L'infrastructure de supervision est entièrement conteneurisée et isolée dans le réseau Docker `achat_devops-net` :
* **Spring Boot Actuator & Micrometer** : Expose les métriques internes de la JVM et du serveur web au format Prometheus sur le point d'accès `/SpringMVC/actuator/prometheus`.
* **Prometheus** : Récupère (scrappe) de manière cyclique (toutes les 15 secondes) les métriques du conteneur applicatif `achat-app:8089`.
* **Grafana** : Fournit une interface de visualisation riche. Le connecteur Prometheus et un tableau de bord JVM sur mesure sont auto-provisionnés dès le démarrage du conteneur.

### 2. Indicateurs Pertinents pour Anticiper les Incidents (KPIs)
Nous avons identifié et configuré 4 catégories clés de métriques :

| Indicateur | Métrique Prometheus | Objectif d'Anticipation |
| :--- | :--- | :--- |
| **Uptime de l'App** | `process_uptime_seconds` | Permet de détecter les redémarrages intempestifs de l'application (crashs de JVM ou OOMKills). |
| **Consommation CPU** | `system_cpu_usage` & `process_cpu_usage` | Anticipe la saturation des ressources processeur avant le ralentissement complet de l'API. |
| **Mémoire JVM (Heap)** | `jvm_memory_used_bytes{area="heap"}` | Détecte les fuites de mémoire (Memory Leaks) avant qu'une exception `OutOfMemoryError` ne survienne. |
| **Activité des Threads** | `jvm_threads_live_threads` | Surveille l'épuisement du pool de threads Tomcat (Thread Starvation) lors de pics de charge. |

---

## 🔒 Partie 2 : Sécurisation de la Chaîne CI/CD (Semaine 6)

L'implémentation DevSecOps intègre la sécurité à chaque étape du cycle de développement logiciel (Secure CI/CD).

### 1. Analyse des Risques & Mesures Implémentées

#### A. Gestion des Entrées Utilisateur (Input Validation)
* **Risque** : Les injections de code (SQL, XSS) si les entrées utilisateur ne sont pas filtrées.
* **Mesure** : L'application utilise **Spring Data JPA / Hibernate** avec des requêtes paramétrées (`@Query` avec des variables d'ancrage comme `:idFacture`), ce qui élimine nativement 100 % des risques d'injections SQL par concaténation de chaînes de caractères.

#### B. Gestion des Erreurs et Journalisation
* **Risque** : L'affichage des traces de pile internes (Stack Traces) à l'utilisateur final (Exposition d'informations).
* **Mesure** : Configuration d'un logger structuré via Logback. En production, les détails d'erreurs techniques sont capturés uniquement dans les fichiers de logs sécurisés du serveur, et non exposés dans les réponses HTTP.

#### C. Exposition de Données Sensibles
* **Risque** : Clés privées, tokens ou mots de passe codés en dur dans le code source.
* **Mesure** : Hardening du `Jenkinsfile` ! Tous les secrets (token de SonarQube, mot de passe de Nexus) sont injectés dynamiquement via des liaisons chiffrées sécurisées (`withCredentials`) stockées dans le coffre-fort de Jenkins. Aucune clé sensible n'est enregistrée en clair dans le dépôt Git.

#### D. Sécurité des Dépendances (SCA)
* **Risque** : Utilisation de bibliothèques tierces obsolètes contenant des failles connues (CVE).
* **Mesure** : Intégration de **SonarQube** et du scanner **Trivy** dans le pipeline pour auditer en continu les vulnérabilités de sécurité présentes dans le projet.

#### E. Sécurité du Pipeline Jenkins
* **Risque** : Exécution de scripts non autorisés (Script Injection) ou accès illimité aux conteneurs de build.
* **Mesure** : Le pipeline est configuré en mode déclaratif strict avec isolation des étapes. L'environnement Jenkins de build s'exécute de manière éphémère et nettoie systématiquement le workspace (`cleanWs()`) après chaque exécution.

#### F. Sécurité de l'Image Docker (Container Hardening)
* **Risques** : 
  1. Utilisation d'une image de base volumineuse contenant des binaires dangereux (ex: compilateurs, shells complets).
  2. Exécution du conteneur en tant qu'utilisateur `root`, facilitant les attaques par évasion de conteneur (Container Breakout).
* **Mesures Implémentées** :
  1. Utilisation de **`eclipse-temurin:17-jre-alpine`** : Une image de base ultra-minimale, contenant uniquement le JRE nécessaire pour exécuter le JAR, réduisant considérablement la surface d'attaque.
  2. Implémentation d'un **utilisateur non-root** dans le `Dockerfile` :
     ```dockerfile
     RUN addgroup -S spring && adduser -S spring -G spring
     USER spring:spring
     ```
     Le conteneur s'exécute désormais avec des privilèges minimums restreints.

#### G. Scanner de Vulnérabilités en Continu (Trivy)
* **Mesure** : Nous avons ajouté une étape automatisée de **Static Container Analysis** dans le `Jenkinsfile` via **Trivy** :
  ```groovy
  stage('Trivy Security Scan') {
      steps {
          sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image --severity HIGH,CRITICAL --scanners vuln achat-app:latest'
      }
  }
  ```
  Chaque build subit un scan automatique détectant les vulnérabilités majeures des packages OS et des dépendances avant tout déploiement.

---

## ⚠️ Partie 3 : Risques Résiduels & OWASP Top 10

Malgré ces solides fondations, certains risques applicatifs restent pertinents au regard du classement **OWASP Top 10** :

### 1. A01:2021-Broken Access Control (Rupture du contrôle d'accès)
* **Risque** : Absence de cadre d'authentification et d'autorisation unifié (Spring Security n'est pas pleinement intégré sur toutes les routes de l'API REST). Un utilisateur malveillant pourrait appeler des endpoints d'achat sensibles directement.
* **Recommandation** : Mettre en œuvre **Spring Security** avec JWT (JSON Web Tokens) et des annotations de sécurité au niveau méthode (ex: `@PreAuthorize("hasRole('ADMIN')")`).

### 2. A05:2021-Security Misconfiguration (Mauvaise configuration de sécurité)
* **Risque** : Des endpoints sensibles de Spring Boot Actuator (comme `/actuator/env` ou `/actuator/shutdown`) pourraient être exposés publiquement s'ils sont mal configurés.
* **Mesure en place** : Nous avons restreint l'exposition à `prometheus,health,info`.
* **Recommandation** : Configurer Spring Security pour restreindre l'accès à l'endpoint `/actuator/**` uniquement aux adresses IP internes de supervision ou à un compte de service d'administration dédié.

### 3. A06:2021-Vulnerable and Outdated Components (Composants vulnérables et obsolètes)
* **Risque** : L'utilisation de dépendances tierces nécessite une mise à jour constante pour éviter les nouvelles vulnérabilités découvertes après la date de déploiement.
* **Recommandation** : Configurer un bot de mise à jour automatique des dépendances (comme Dependabot sur GitHub) et intégrer l'outil **OWASP Dependency-Check** sous forme de plugin Maven dans le pipeline Jenkins.

---

## 🎯 Conclusion & Prochaines Étapes
La chaîne CI/CD de votre projet **Achat** est désormais une référence en matière de DevSecOps académique :
1. **Supervision opérationnelle** en temps réel via Prometheus/Grafana.
2. **Hardening de l'image Docker** validé (non-root user).
3. **Analyse de sécurité SAST et de conteneur** automatisée (SonarQube + Trivy).
