# Project Understanding – Achat Application

## 1. Context

This is a **Spring Boot 2.5.3** REST API application developed for ESPRIT as a DevOps module base project.  
It is a **Purchase Management (Achat)** back-end, exposing REST endpoints for managing suppliers, products, invoices, payments, stock and operators.

---

## 2. Architecture Overview

```
┌──────────────────────────────────────────────────────────────────┐
│                      REST Controllers (8)                        │
│  Fournisseur │ Facture │ Produit │ Reglement │ Stock │ Operateur │
│  CategorieProduit │ SecteurActivite                              │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                    Service Layer (16 classes)                     │
│  Interface + Impl pattern for each domain entity                  │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│              Repository Layer (JPA Repositories – 10)            │
└──────────────────────────┬───────────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────────┐
│                  MySQL 5 Database (achatdb)                      │
└──────────────────────────────────────────────────────────────────┘
```

### Technology Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 2.5.3 |
| Language | Java 8 |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 5 (port 3306) |
| API Docs | Springfox Swagger 3.0 |
| Build | Maven 3 |
| Utilities | Lombok |

---

## 3. Domain Model

```
Fournisseur ──┬── DetailFournisseur (1-1)
              ├── SecteurActivite (M-M)
              └── Facture (1-M)
                   ├── Operateur (M-M)
                   ├── Reglement (1-M)
                   └── DetailFacture (1-M)
                        └── Produit (M-1)
                             ├── Stock (M-1)
                             └── CategorieProduit (M-1)
```

### Entities (11 total)

| Entity | Key Fields |
|--------|-----------|
| `Fournisseur` | code, libelle, categorieFournisseur |
| `DetailFournisseur` | dateDebutCollaboration, email, adresse |
| `SecteurActivite` | codeSecteurActivite, libelleSecteurActivite |
| `Facture` | montantFacture, montantRemise, archivee, dateCreationFacture |
| `DetailFacture` | qteCommandee, prixUnitaire, pourcentageRemise |
| `Reglement` | montantPaye, dateReglement, payee |
| `Produit` | codeProduit, libelleProduit, prix |
| `Stock` | libelleStock, qteStock, qteStockMin |
| `CategorieProduit` | codeCategorie, libelleCategorie |
| `Operateur` | nom, prenom, password |

---

## 4. REST API Endpoints

The application runs on **port 8089** with context path `/SpringMVC`.  
Swagger UI is available at: `http://localhost:8089/SpringMVC/swagger-ui/`

| Resource | Base URL |
|----------|---------|
| Fournisseur | `/fournisseur` |
| Facture | `/facture` |
| Produit | `/produit` |
| Reglement | `/reglement` |
| Stock | `/stock` |
| Operateur | `/operateur` |
| CategorieProduit | `/categorieProduit` |
| SecteurActivite | `/secteurActivite` |

---

## 5. Identified Weaknesses & Technical Limits

### Code Quality
- No unit tests exist (`src/test` is empty)
- `@Autowired` field injection used everywhere (not constructor injection)
- Potential `NullPointerException` in `getFacturesByFournisseur` when fournisseur is null
- `assignOperateurToFacture` will NPE if operateur is null
- `pourcentageRecouvrement` will throw ArithmeticException if total factures = 0
- Raw unchecked casts: `(List<Facture>)` on repository results

### Security
- DB password is empty and committed to `application.properties`
- No authentication/authorization mechanism (no Spring Security)
- Swagger UI is publicly exposed

### DevOps / Organisational
- No CI/CD pipeline
- No containerisation
- No quality gate (Sonar)
- No artifact management
- No monitoring/supervision

---

## 6. Improvement Plan (DevOps Transformation)

| Week | Focus |
|------|-------|
| 1 | Git structuring, branching, backlog |
| 2 | Jenkins pipeline (build + test) |
| 3 | SonarQube analysis + Nexus artifact publication |
| 4 | Docker containerisation |
| 5 | Prometheus + Grafana monitoring |
| 6 | Security hardening (OWASP Top 10) |
