# Jenkins Setup Guide (via Docker)

## Step 1 — Start the Infrastructure

Open **PowerShell** or **CMD** in the project folder and run:

```bash
cd c:\Users\azizo\Downloads\achat\achat
docker-compose -f docker-compose-infra.yml up -d
```

Wait ~60 seconds for all containers to start. Check status:

```bash
docker ps
```

You should see 5 containers running: `jenkins`, `sonarqube`, `sonar-db`, `nexus`, `achat-mysql`.

---

## Step 2 — Get the Jenkins Initial Admin Password

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copy the password printed.

---

## Step 3 — First Jenkins Login

1. Open your browser → **http://localhost:8080**
2. Paste the admin password from Step 2
3. Click **"Install suggested plugins"** and wait for installation
4. Create your **admin user** (e.g. admin / admin123)
5. Set Jenkins URL to `http://localhost:8080/` → click **Save and Finish**

---

## Step 4 — Install Required Plugins

Go to **Manage Jenkins → Plugin Manager → Available** and install:

| Plugin | Purpose |
|--------|---------|
| `Pipeline` | Declarative pipeline support |
| `Git` | Git SCM integration |
| `Maven Integration` | Maven build steps |
| `JaCoCo` | Code coverage reports |
| `SonarQube Scanner` | Sonar analysis in pipeline |
| `Nexus Artifact Uploader` | Publish JARs to Nexus |
| `Docker Pipeline` | Docker commands in pipeline |
| `Blue Ocean` | (Optional) Beautiful pipeline UI |
| `Email Extension` | Email notifications |

Click **"Download now and install after restart"** → check **"Restart Jenkins when done"**.

---

## Step 5 — Configure Global Tools

Go to **Manage Jenkins → Global Tool Configuration**:

### JDK
- Click **Add JDK**
- Name: `JDK-8`
- Uncheck "Install automatically"
- JAVA_HOME: (inside the Jenkins container it's `/opt/java/openjdk`)
  - Or tick "Install automatically" → choose JDK 8 from AdoptOpenJDK

### Maven
- Click **Add Maven**
- Name: `Maven-3`
- Tick **"Install automatically"** → select Maven 3.8.x

---

## Step 6 — Configure SonarQube in Jenkins

1. Go to **Manage Jenkins → Configure System**
2. Scroll to **SonarQube Servers**
3. Click **Add SonarQube**:
   - Name: `SonarQube`
   - Server URL: `http://sonarqube:9000`  *(use container name, not localhost)*
   - Server authentication token: (generate one in Step 7 first)

---

## Step 7 — Get SonarQube Token

1. Open **http://localhost:9000** (default login: `admin` / `admin`)
2. Go to **My Account → Security → Generate Token**
3. Name: `jenkins-token` → click **Generate**
4. Copy the token
5. Back in Jenkins: **Manage Jenkins → Credentials → System → Global**
   - Add → **Secret text**
   - Secret: paste the token
   - ID: `sonar-token`
   - Description: `SonarQube token for Jenkins`
6. Go back to **Configure System → SonarQube Servers** and select this credential

---

## Step 8 — Configure Nexus Credentials in Jenkins

1. **Manage Jenkins → Credentials → System → Global → Add**
2. Kind: **Username with password**
   - Username: `admin`
   - Password: (see Step 8b)
   - ID: `nexus-credentials`

### 8b — Get Nexus Admin Password

```bash
docker exec nexus cat /nexus-data/admin.password
```

3. Open **http://localhost:8081** → login with `admin` + that password
4. Follow the setup wizard → set new password → enable anonymous access

### Create Nexus Repositories

In Nexus UI → **Settings (⚙) → Repositories → Create repository**:

| Name | Type | Format |
|------|------|--------|
| `achat-releases` | hosted | maven2 |
| `achat-snapshots` | hosted | maven2 |

---

## Step 9 — Create the Jenkins Pipeline Job

1. Jenkins home → **New Item**
2. Name: `achat-pipeline`
3. Type: **Pipeline** → OK
4. **Build Triggers**: check **"GitHub hook trigger for GITScm polling"** (or Poll SCM: `H/5 * * * *`)
5. **Pipeline**:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: your Git repo URL
   - Branch: `*/develop`
   - Script Path: `Jenkinsfile`
6. Click **Save**

---

## Step 10 — Run the Pipeline

Click **"Build Now"** on the `achat-pipeline` job.

Watch the stages execute in **Blue Ocean** or the classic stage view.

---

## Service URLs Summary

| Service | URL | Default Login |
|---------|-----|--------------|
| Jenkins | http://localhost:8080 | admin / (from step 2) |
| SonarQube | http://localhost:9000 | admin / admin |
| Nexus | http://localhost:8081 | admin / (from step 8b) |
| App (after deploy) | http://localhost:8089/SpringMVC | — |

---

## Troubleshooting

### Jenkins container not starting?
```bash
docker logs jenkins
```

### SonarQube crash (often vm.max_map_count)?
Run in PowerShell **as Administrator**:
```powershell
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144
exit
```
Or on Linux host: `sudo sysctl -w vm.max_map_count=262144`

### Maven build inside pipeline fails (mvn not found)?
Make sure the tool name in Jenkinsfile (`Maven-3`) exactly matches what you configured in Global Tool Configuration.
