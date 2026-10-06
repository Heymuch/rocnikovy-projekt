# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

University year project (ročníkový projekt, UPCE) built by a team of four. Project documentation (READMEs, diagrams) and code comments are written in Czech.

Planned architecture (see the mermaid diagram in `README.md`): Angular frontend (`web/`) → Ktor/Kotlin backend (`server/`, port 8080) → PostgreSQL, with Keycloak for auth. `server/` is still only a README stub; `web/` is a freshly generated Angular 21 app. The working parts so far are the database stack and the OpenStreetMap railway scripts.

## Local stack

Runs with **Podman** (not Docker) via `docker-compose.yml`:

```sh
make up     # podman compose up — postgres, one-shot flyway migrate, pgAdmin, overpass
make down   # podman compose down — keeps volumes
podman compose down --volumes   # wipe the database (needed after changing database/init/)
```

- PostgreSQL: `127.0.0.1:5432`, db `rocnikovy_projekt`, user `app` / `password`
- pgAdmin: http://127.0.0.1:5050 (`admin@example.com` / `password`), server preconfigured from `database/pgadmin/servers.json`
- Overpass API: `127.0.0.1:8888` — local instance initialized from a Geofabrik Czech Republic extract (`OVERPASS_STOP_AFTER_INIT`, so the first run only builds the `overpass-db` volume and is slow). The OSM scripts do not use it yet; they hardcode public Overpass servers.
- Credentials/ports are overridable via env vars (`POSTGRES_*`, `FLYWAY_*`, `PGADMIN_*`, `OVERPASS_PORT`).

## Database & migrations

- Migrations are plain SQL in `database/sql/`, named `V<major>_<minor>_<patch>__<description>.sql` (e.g. `V001_000_000__create_consignment_table.sql`).
- Flyway mounts the whole `database/` directory as its location and scans recursively, so any `V*.sql` file anywhere under `database/` is picked up.
- Migrations run as a dedicated `flyway` role, not as `app`. That role is created by `database/init/01-create-flyway-user.sh`, which the postgres image executes **only when the data volume is first initialized**.
- Never edit an already-applied migration; add a new versioned file instead (Flyway checksum validation fails otherwise).
- SQL style: lowercase keywords, double-quoted identifiers, plural table names.

## OpenStreetMap railway pipeline (`openstreetmap/`)

Python 3 stdlib only. Default paths are relative (`./json/`, `./csv/`) and scripts import `rail_lengths`, so **run them from inside `openstreetmap/`**. Outputs (`*.json`, `*.csv`) are gitignored.

```sh
python3 get_stations.py        # Overpass → json/railway_stations.json
python3 get_rails.py           # Overpass → json/railway_rails.json (rail + narrow_gauge, no service tracks)
python3 rail_segments.py       # rails → csv/rail_segments.csv (point-to-point edges, both directions, metres)
python3 station_segments.py --max-distance 60   # splices stations into rail_segments.csv IN PLACE
python3 station_path.py --from "Cheb" --to node/3036772660   # Dijkstra shortest path; prompts if args omitted
```

The rail graph is `csv/rail_segments.csv` (`lat_of_a,lon_of_a,lat_of_b,lon_of_b,distance_in_meters`); nodes are GPS points, and stations become nodes after `station_segments.py`. Because that step rewrites the CSV, re-run `rail_segments.py` before running it again. `openstreetmap/Makefile` is partly stale (references scripts that no longer exist); only `station_segments` and the scripts above are valid targets.

## Web (`web/`)

Angular 21 CLI project, npm, Vitest via `@angular/build:unit-test`. From `web/`: `npm start` (dev server :4200), `npm run build`, `npm test`. Prettier: 100 cols, single quotes. Component prefix `app`, SCSS styles.

## Conventions

- `.editorconfig`: LF line endings, 4-space indent, final newline; tabs in `Makefile`.
