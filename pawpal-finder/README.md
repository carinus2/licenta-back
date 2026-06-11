# PawPal Finder - Backend

[![CI Pipeline](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml/badge.svg)](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml)

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

Acest proiect utilizează GitHub Actions pentru integrare continuă.

### Workflow Automat

**CI Pipeline (`ci.yml`)** - Rulează automat la fiecare push sau pull request pe branch-urile `main` și `develop`:

- ✅ Compilează codul sursă folosind Maven
- ✅ Rulează testele unitare (AnimalServiceTest)
- ✅ Încarcă rapoartele de testare ca artifacts

### Cum să Rulezi Testele Local

**Teste Unitare (compatibile cu CI):**
```bash
cd pawpal-finder
./mvnw test -Dtest=AnimalServiceTest
```

**Teste de Integrare (necesită Vault):**
Testele de integrare necesită HashiCorp Vault să fie pornit. Pentru a le rula local:
```bash
# Pornește Vault mai întâi (vezi DEVOPS_SECRETS_MANAGEMENT.md)
docker-compose up -d vault

# Apoi rulează testele de integrare
./mvnw test -Dtest=AnimalControllerIntegrationTest
```

**Notă:** Pipeline-ul CI rulează doar testele unitare care nu necesită dependențe externe precum Vault sau PostgreSQL.
