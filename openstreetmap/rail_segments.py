#!/usr/bin/env python3
"""Export distances between consecutive geometry points of railway rails.

Reads rails from railway_rails_optimized.json and for every pair of
consecutive points of a rail geometry writes two CSV records (there and
back) in the format: lat_of_a,lon_of_a,lat_of_b,lon_of_b,distance_in_meters
"""
import argparse
import csv
import json
import sys
from pathlib import Path

from rail_lengths import haversine

DEFAULT_RAILS = "./json/railway_rails.json"
DEFAULT_OUTPUT = "./csv/rail_segments.csv"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-i", "--input", type=Path, default=DEFAULT_RAILS, help="Input JSON file with rails")
    parser.add_argument("-o", "--output", type=Path, default=DEFAULT_OUTPUT, help="Output CSV file")
    args = parser.parse_args()

    rails = json.loads(args.input.read_text(encoding="utf-8")).get("elements", [])

    # Překrývající se cesty mohou sdílet stejné úseky, každý se zapíše jen jednou
    seen = set()
    with args.output.open("w", encoding="utf-8", newline="") as file:
        writer = csv.writer(file, lineterminator="\n")
        writer.writerow(["lat_of_a", "lon_of_a", "lat_of_b", "lon_of_b", "distance_in_meters"])
        for rail in rails:
            geometry = rail.get("geometry") or []
            for a, b in zip(geometry, geometry[1:]):
                pa, pb = (a["lat"], a["lon"]), (b["lat"], b["lon"])
                if pa == pb or (pa, pb) in seen:
                    continue
                seen.add((pa, pb))
                seen.add((pb, pa))
                distance = round(haversine(*pa, *pb), 2)
                writer.writerow([*pa, *pb, distance])
                writer.writerow([*pb, *pa, distance])

    print(f"{len(seen)} records from {len(rails)} rails were saved into {args.output}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
