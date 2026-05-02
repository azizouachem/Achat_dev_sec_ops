## Branch Strategy – Achat DevOps Project

### Main Branches

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready code only. Protected – no direct push. |
| `develop` | Integration branch. All features merge here first. |

### Supporting Branches

| Pattern | Purpose | Merges into |
|---------|---------|-------------|
| `feature/<ticket-id>-short-desc` | New functionality | `develop` |
| `bugfix/<ticket-id>-short-desc` | Bug corrections | `develop` |
| `release/<version>` | Release preparation & QA | `main` + back to `develop` |
| `hotfix/<ticket-id>-short-desc` | Critical prod fixes | `main` + back to `develop` |

### Workflow (GitFlow Lite)

```
feature/* ──► develop ──► release/* ──► main
                                        │
hotfix/* ──────────────────────────────►┘
```

### Commit Convention (Conventional Commits)

```
<type>(<scope>): <short summary>

Types: feat | fix | test | ci | docs | chore | refactor | perf
```

**Examples:**
```
feat(fournisseur): add service implementation for secteur-activite assignment
test(produit): add JUnit tests for addProduit and updateProduit
ci(jenkins): add sonarqube analysis stage to pipeline
fix(facture): handle null pointer in getFacturesByFournisseur
```

### Protected Branch Rules

- `main` and `develop` require a Pull Request with at least 1 reviewer approval.
- CI pipeline must pass before merge.
- No force-push allowed on protected branches.

### Versioning

Follows **Semantic Versioning (SemVer)**: `MAJOR.MINOR.PATCH`  
Current version: `1.0`
