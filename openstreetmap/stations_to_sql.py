#!/usr/bin/env python3
"""Generate SQL INSERTs into the stations table from railway_stations.json.

A station is a node of the railway graph and shares its ID, which is derived
from its GPS position the same way as in segments_to_sql.py. Only stations
that are nodes of the rail graph (were linked by station_segments.py) are
written, the others would violate the foreign key to railway_nodes.
"""
import argparse
import csv
import json
import sys
from pathlib import Path

from segments_to_sql import get_uuid_v5

DEFAULT_STATIONS = "./json/railway_stations.json"
DEFAULT_SEGMENTS = "./csv/segments.csv"
DEFAULT_OUTPUT = "./sql/stations.sql"


def sql_string(value: str) -> str:
    return "'" + value.replace("'", "''") + "'"


def station_to_sql(station) -> str:
    return f"""INSERT INTO stations(id, name) VALUES ('{str(station["id"])}', {sql_string(station["name"])});"""


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-s", "--stations", type=Path, default=DEFAULT_STATIONS, help="Input JSON file with stations")
    parser.add_argument("-i", "--input", type=Path, default=DEFAULT_SEGMENTS, help="Input CSV file with segments (graph nodes)")
    parser.add_argument("-o", "--output", type=Path, default=DEFAULT_OUTPUT, help="Output SQL file")
    args = parser.parse_args()

    stations = json.loads(args.stations.read_text(encoding="utf-8")).get("elements", [])

    # ID všech uzlů grafu, stanice mimo graf by porušila cizí klíč na railway_nodes
    with args.input.open(encoding="utf-8", newline="") as file:
        reader = csv.reader(file)
        next(reader)
        nodes = set()
        for row in reader:
            nodes.add(get_uuid_v5((float(row[0]), float(row[1]))))
            nodes.add(get_uuid_v5((float(row[2]), float(row[3]))))

    records = []
    names = set()
    skipped = 0
    for station in stations:
        name = station.get("tags", {}).get("name")
        if not name:
            skipped += 1
            print(f"Skipped node/{station['id']}: no name", file=sys.stderr)
            continue
        if name in names:
            skipped += 1
            print(f"Skipped {name} (node/{station['id']}): duplicate name", file=sys.stderr)
            continue
        node_id = get_uuid_v5((station["lat"], station["lon"]))
        if node_id not in nodes:
            skipped += 1
            print(f"Skipped {name}: not a node of the rail graph", file=sys.stderr)
            continue
        names.add(name)
        records.append({"id": node_id, "name": name})

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text("\n".join(station_to_sql(record) for record in records) + "\n", encoding="utf-8")
    print(f"{len(records)} records were saved into {args.output}, {skipped} skipped", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
