# Product Backlog – Achat DevOps Project

## Team Composition

| Role | Responsibility |
|------|---------------|
| Product Owner | Prioritises backlog, validates deliverables |
| Scrum Master | Facilitates sprints, removes blockers |
| Dev / DevOps 1 | Git, Jenkins, pipeline |
| Dev / DevOps 2 | Tests, Sonar, Nexus, Docker |

---

## Definition of Done

- Code reviewed and merged into `develop`
- CI pipeline passes (build + tests)
- Documentation updated
- Demo prepared

---

## Epics & User Stories

### EPIC 1 – Git & Project Setup

| ID | User Story | Priority | Estimate |
|----|-----------|----------|----------|
| US-01 | As a dev, I want a Git repo with a clear branching strategy so the team can collaborate without conflicts | HIGH | 2h |
| US-02 | As a dev, I want a `.gitignore` that excludes build artifacts and secrets so sensitive data is never committed | HIGH | 1h |
| US-03 | As PO, I want a project understanding document so the team knows the architecture before changing anything | HIGH | 3h |
| US-04 | As SM, I want a Product Backlog with User Stories so the team has a clear road-map | HIGH | 2h |

---

### EPIC 2 – Jenkins CI Pipeline

| ID | User Story | Priority | Estimate |
|----|-----------|----------|----------|
| US-05 | As a dev, I want Jenkins to pull code from Git on every push so integration issues are detected immediately | HIGH | 3h |
| US-06 | As a dev, I want a `Jenkinsfile` with a Build stage so the artifact is compiled automatically | HIGH | 2h |
| US-07 | As a dev, I want a `Jenkinsfile` with a Test stage so unit test results are visible in Jenkins | HIGH | 2h |
| US-08 | As PO, I want email notifications on pipeline failure so the team reacts quickly | MEDIUM | 1h |

---

### EPIC 3 – Tests Automatisés

| ID | User Story | Priority | Estimate |
|----|-----------|----------|----------|
| US-09 | As a dev, I want unit tests for `ProduitServiceImpl` (CRUD) so regressions are caught early | HIGH | 4h |
| US-10 | As a dev, I want unit tests for `FournisseurServiceImpl` so the supplier logic is validated | HIGH | 3h |
| US-11 | As a dev, I want unit tests for `FactureServiceImpl` (pourcentageRecouvrement, cancelFacture) so financial calculations are reliable | HIGH | 4h |
| US-12 | As PO, I want a test coverage report in Jenkins so I can see the quality trend | MEDIUM | 2h |

---

### EPIC 4 – SonarQube Quality Gate

| ID | User Story | Priority | Estimate |
|----|-----------|----------|----------|
| US-13 | As a dev, I want SonarQube integrated in the pipeline so code smells are reported automatically | HIGH | 3h |
| US-14 | As PO, I want a quality gate that blocks the pipeline on critical issues so bad code never reaches `develop` | HIGH | 2h |
| US-15 | As a dev, I want NPE bugs identified by Sonar fixed so the application is more robust | HIGH | 3h |

---

### EPIC 5 – Nexus Artifact Management

| ID | User Story | Priority | Estimate |
|----|-----------|----------|----------|
| US-16 | As a dev, I want Maven configured to publish JARs to Nexus so every build is traceable | HIGH | 2h |
| US-17 | As PO, I want versioned releases in Nexus so we can roll back at any time | HIGH | 2h |

---

### EPIC 6 – Docker & Deployment

| ID | User Story | Priority | Estimate |
|----|-----------|----------|----------|
| US-18 | As a dev, I want a `Dockerfile` so the application runs in any environment | HIGH | 3h |
| US-19 | As a dev, I want a `docker-compose.yml` with app + MySQL so the full stack starts with one command | HIGH | 3h |
| US-20 | As PO, I want the Docker image built and pushed in the Jenkins pipeline so deployment is automated | HIGH | 2h |

---

## Sprint Backlog

### Sprint 1 (Week 1) – Git & Cadrage
- [x] US-01 Git repo + branching strategy
- [x] US-02 .gitignore
- [x] US-03 Project understanding document
- [x] US-04 Product backlog

### Sprint 2 (Week 2) – Jenkins
- [ ] US-05 Jenkins + Git connection
- [ ] US-06 Build stage
- [ ] US-07 Test stage
- [ ] US-08 Email notification

### Sprint 3 (Week 3) – Quality + Nexus
- [ ] US-09 Tests ProduitService
- [ ] US-10 Tests FournisseurService
- [ ] US-11 Tests FactureService
- [ ] US-12 Coverage report
- [ ] US-13 SonarQube integration
- [ ] US-14 Quality gate
- [ ] US-15 Bug fixes
- [ ] US-16 Nexus config
- [ ] US-17 Versioned releases

### Sprint 4 (Week 4) – Docker
- [ ] US-18 Dockerfile
- [ ] US-19 docker-compose.yml
- [ ] US-20 Docker in pipeline
