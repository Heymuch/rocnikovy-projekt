#!/usr/bin/env bash
# Creates a dedicated user for running Flyway migrations.
# Executed by the postgres image only when the data directory is initialized for the first time.
#set -euo pipefail

psql -v ON_ERROR_STOP=1 \
    --username "$POSTGRES_USER" \
    --dbname "$POSTGRES_DB" \
    -v flyway_user="$FLYWAY_USER" \
    -v flyway_password="$FLYWAY_PASSWORD" \
    -v db="$POSTGRES_DB" <<-'EOSQL'
    CREATE ROLE :"flyway_user" WITH LOGIN PASSWORD :'flyway_password';
    GRANT CONNECT, CREATE, TEMPORARY ON DATABASE :"db" TO :"flyway_user";
    GRANT ALL ON SCHEMA public TO :"flyway_user";
EOSQL
