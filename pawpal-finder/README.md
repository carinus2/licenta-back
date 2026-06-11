# PawPal Finder - Backend

[![CI Pipeline](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml/badge.svg)](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml)
[![Code Quality](https://github.com/carinus2/licenta-back/actions/workflows/code-quality.yml/badge.svg)](https://github.com/carinus2/licenta-back/actions/workflows/code-quality.yml)

## PAȘII DE COMPILARE, INSTALARE ȘI LANSARE AI APLICAȚIEI

#### Adresele repository-urilor de pe github.com:

1. https://github.com/carinus2/licenta-back.git - pentru backend
2. https://github.com/carinus2/licenta-ui.git - pentru frontend

Pentru clonarea proiectelor pe mediul de dezvoltare local se va folosi comanda „git clone https://github.com/carinus2/licenta-back.git” și „git clone https://github.com/carinus2/licenta-ui.git”.

#### Configurarea backend-ului

Se va deschide într-un mediu de dezvoltare integrat, cum ar fi Intellij, proiectul de backend. Ca versiune implicită de Java se va folosi Java 21. Înainte de rularea și testarea aplicației, este necesar ca în sistem să fie instalat Apache Maven.

De asemenea, trebuie setate variabilele de mediu necesare pentru conectarea la baza de date și alte servicii, precum:

* `DB_URL`
* `DB_USERNAME`
* `DB_PASSWORD`

După configurare, din terminal, în directorul proiectului `licenta-back`, se va rula: **mvn clean install**. Aplicația se pornește prin rularea clasei **PawpalFinderApplication**, fiind disponibilă la portul 8080.

#### Configurarea frontend-ului

Pentru frontend, proiectul se va deschide într-un editor compatibil, precum Visual Studio Code. Este necesar ca în sistem să fie instalate **Node.js** și **npm**. În directorul `licenta-ui`, se vor rula următoarele comenzi: **npm install** și **npm run start**.

Aplicația va fi disponibilă la adresa: [http://localhost:4200](http://localhost:4200).

#### Configurarea bazei de date

Pentru funcționarea completă a aplicației, este necesară configurarea unei baze de date PostgreSQL. Se recomandă instalarea atât a **PostgreSQL**, cât și a interfeței grafice **pgAdmin**.


## CI/CD Pipeline

Acest proiect utilizează GitHub Actions pentru integrare continuă și verificarea calității codului.

### Workflow-uri Automate

#### 1. CI Pipeline (`ci.yml`)
Rulează automat la fiecare push sau pull request pe branch-urile `main` și `develop`:

- **Build și Compilare**: Compilează codul sursă folosind Maven
- **Teste Automate**: Rulează toate testele unitare și de integrare
- **Rapoarte de Testare**: Generează și încarcă rapoarte detaliate de testare
- **Build Artifact**: Creează fișierul JAR al aplicației
- **PostgreSQL Service**: Configurează o bază de date PostgreSQL pentru teste

#### 2. Code Quality (`code-quality.yml`)
Verifică calitatea codului:

- **Checkstyle Analysis**: Verifică respectarea standardelor de cod Java
- **Dependency Check**: Analizează dependențele pentru vulnerabilități
- **Dependency Tree**: Generează arborele de dependențe

### Cum să Rulezi Testele Local

```bash
# Rulează toate testele
mvn test

# Rulează testele cu raport de acoperire
mvn clean test jacoco:report

# Verifică calitatea codului
mvn checkstyle:check

# Analizează dependențele
mvn dependency:analyze
```

### Rapoarte Generate

După rularea testelor, rapoartele sunt disponibile în:
- **Test Reports**: `target/surefire-reports/`
- **Coverage Reports**: `target/site/jacoco/`
- **Checkstyle Reports**: `target/checkstyle-result.xml`
