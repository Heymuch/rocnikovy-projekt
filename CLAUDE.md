# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

University year project (ročníkový projekt, UPCE) built by a team of four. Project documentation (READMEs, diagrams) and code comments are written in Czech.

Planned architecture (see the mermaid diagram in `README.md`): Angular frontend (`web/`) → Ktor/Kotlin backend (`server/`, port 8080) → PostgreSQL, with Keycloak for auth. `web/` is a freshly generated Angular 21 app. The working parts so far are the database stack, the OpenStreetMap railway scripts and a first server endpoint (station search).

## Local stack

Runs with **Podman** (not Docker) via `docker-compose.yml`:

```sh
make up     # podman compose up — postgres + pgAdmin (compose project `feicargo`)
make down   # podman compose down --volumes — also WIPES the database
```

- PostgreSQL: `127.0.0.1:5432`, db `feicargo`, user `app` / `password`
- pgAdmin: http://127.0.0.1:5050 (`admin@example.com` / `password`), server preconfigured from `database/pgadmin/servers.json`
- Credentials/ports are overridable via env vars (`POSTGRES_*`, `FLYWAY_*`, `PGADMIN_*`).
- There is no migration container: the server runs Flyway on startup (see below), so the schema exists only after the server has been started once.

## Server (`server/`)

Kotlin + Ktor (Netty, `EngineMain`), Gradle 9.8 with version catalogs (`gradle/libs.versions.toml` + `ktorLibs`), root project `fei-cargo`. Run from `server/` via `./gradlew` (`./gradlew run`, `./gradlew build`, `./gradlew ktlintCheck`/`ktlintFormat`).

- Config in `src/main/resources/application.yaml`: Ktor modules are loaded in order `databaseModule` → `dependencyInjectionModule` → `routingModule`; `postgresql.*` (app connection) and `flyway.*` sections.
- `application/Database.kt`: on startup runs Flyway over JDBC as role `flyway` (Flyway has no R2DBC support), then connects Exposed via an R2DBC connection pool as role `app`. Requires a running postgres.
- Dependency injection uses Ktor's DI plugin (`application/di/` registers repositories, use cases, controllers); routes resolve controllers via `application.dependencies.resolve<…>()`.
- Package layout per feature (`station/`, `route/`): `entity`, `usecase`, `persistence` (+ `persistence/exposed`), `api/rest` (`Stations` resources, `route`, `controller`, `dto`). Shared abstractions live in `commons/`.
- Station search uses PostgreSQL fulltext (`stations.search_vector`, unaccented `tsvector`) via custom Exposed helpers in `commons/persistence/exposed/`.

## Database & migrations

- The target data model (orders, cargo, wagons, locomotives, train sets, stations, railway graph/nodes) is the mermaid ER diagram in `database/README.md`.
- Migrations are plain SQL in `server/src/main/resources/database/migration/` (`structure/` for schema, `data/` for seed data), loaded from `classpath:database/migration`, named `V<major>_<minor>_<patch>__<description>.sql` (e.g. `V01_00_00__initial_stucture.sql`, `V01_00_10__insert_railway_nodes.sql`).
- The data migrations (railway nodes, graph, stations; ~90 MB) are copies of the SQL generated in `openstreetmap/sql/` — regenerate there and copy over as a new migration version.
- Migrations run as a dedicated `flyway` role, not as `app`. That role is created by `database/init/01-create-flyway-user.sh`, which the postgres image executes **only when the data volume is first initialized**.
- Never edit an already-applied migration; add a new versioned file instead (Flyway checksum validation fails otherwise).
- SQL style: lowercase keywords, double-quoted identifiers, plural table names.

## OpenStreetMap railway pipeline (`openstreetmap/`)

Python 3 stdlib only. Default paths are relative (`./json/`, `./csv/`) and scripts import `rail_lengths`, so **run them from inside `openstreetmap/`**. Outputs (`*.json`, `*.csv`) are gitignored.

```sh
python3 get_stations.py        # Overpass → json/railway_stations.json
python3 get_rails.py           # Overpass → json/railway_rails.json (rail + narrow_gauge, no service tracks)
python3 rail_segments.py       # rails → csv/rail_segments.csv (undirected point-to-point edges, each stored once, metres)
python3 station_segments.py --max-distance 60   # splices stations into rail_segments.csv IN PLACE
python3 station_path.py --from "Cheb" --to node/3036772660   # Dijkstra shortest path; prompts if args omitted
python3 segments_to_sql.py -i csv/rail_segments.csv   # → sql/railway_nodes.sql + sql/railway_graph.sql (default input csv/segments.csv)
python3 stations_to_sql.py -i csv/rail_segments.csv   # → sql/stations.sql (only named, unique stations that are graph nodes)
```

`segments_to_sql.py` derives node IDs as `uuid5(NIL, "<int(lat*1e7)>,<int(lon*1e7)>")`, so the same GPS point always gets the same UUID; `stations_to_sql.py` reuses it so a station shares its node's ID (FK `stations.id → railway_nodes.id`). Output paths of `segments_to_sql.py` are hardcoded. The generated `openstreetmap/sql/*.sql` files are committed, unlike the JSON/CSV outputs.

The rail graph is `csv/rail_segments.csv` (`lat_of_a,lon_of_a,lat_of_b,lon_of_b,distance_in_meters`); nodes are GPS points, and stations become nodes after `station_segments.py`. Because that step rewrites the CSV, re-run `rail_segments.py` before running it again. `openstreetmap/Makefile` is partly stale (references scripts that no longer exist); only `station_segments`, `station_path`, `segments_to_sql` and `stations_to_sql` are valid targets.

## Web (`web/`)

Angular 21 CLI project, npm, Vitest via `@angular/build:unit-test`. From `web/`: `npm start` (dev server :4200), `npm run build`, `npm test`. Prettier: 100 cols, single quotes. Component prefix `app`, SCSS styles.

## Conventions

- `.editorconfig`: LF line endings, 4-space indent, final newline; tabs in `Makefile`.
