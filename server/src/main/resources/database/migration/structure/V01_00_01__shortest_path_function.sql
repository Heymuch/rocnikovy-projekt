-- Hledání nejkratší cesty v železniční síti pomocí pgRouting (pgr_dijkstra).
-- Rozšíření není "trusted", role flyway ho vytvořit nesmí – zakládá ho superuživatel
-- v database/init/02-create-extensions.sql. Tady jen ověříme, že existuje.
create extension if not exists "pgrouting" cascade;

-- pgRouting pracuje jen s celočíselnými ID vrcholů, uzly mají UUID → doplníme číselné ID
alter table "railway_nodes"
    add column "vertex_id" bigint generated always as identity;

alter table "railway_nodes"
    add constraint "railway_nodes_vertex_id_key" unique ( "vertex_id" );

-- Nejkratší cesta mezi dvěma uzly; vrací uzly v pořadí od "p_from" po "p_to" (včetně).
-- Hrany jsou uložené jen jednou a jsou neorientované (directed => false).
-- Pokud cesta neexistuje, nevrací žádný řádek; pro "p_from" = "p_to" vrací jeden uzel.
create or replace function "find_shortest_path"(
    "p_from" uuid,
    "p_to" uuid
) returns table (
    "seq"     integer,
    "node_id" uuid
)
    language sql
    stable
as $$
    select 1, "id"
    from "railway_nodes"
    where "id" = "p_from" and "p_from" = "p_to"

    union all

    select "path"."path_seq", "nodes"."id"
    from pgr_dijkstra(
        $edges$
            select
                row_number() over () as "id",
                "a"."vertex_id" as "source",
                "b"."vertex_id" as "target",
                "g"."distance"::double precision as "cost"
            from "railway_graph" as "g"
            join "railway_nodes" as "a" on "a"."id" = "g"."node_a_id"
            join "railway_nodes" as "b" on "b"."id" = "g"."node_b_id"
        $edges$,
        ( select "vertex_id" from "railway_nodes" where "id" = "p_from" ),
        ( select "vertex_id" from "railway_nodes" where "id" = "p_to" ),
        directed => false
    ) as "path"
    join "railway_nodes" as "nodes" on "nodes"."vertex_id" = "path"."node"
    where "p_from" <> "p_to"

    order by 1
$$;
