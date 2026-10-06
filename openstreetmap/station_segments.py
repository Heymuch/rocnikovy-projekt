#!/usr/bin/env python3
"""Connect railway stations to all rail segments within a maximum distance.

Reads stations from railway_stations.json and rail segments from
rail_segments.csv. For every station finds all segments (pairs of points
A, B) not farther than --max-distance, removes each of them and replaces it
with segments A - station and station - B (there and back). Linking to every
nearby segment (not only the nearest one) connects the station to all its
tracks, so the graph is not split at stations with several parallel tracks.
Several stations on the same segment are chained in order along it. When the
nearest place of a segment is its end point, the station is only linked to
that point and the segment is kept.

The CSV file is rewritten in place in the format:
lat_of_a,lon_of_a,lat_of_b,lon_of_b,distance_in_meters
"""
import argparse
import csv
import json
import math
import sys
from collections import defaultdict
from pathlib import Path

from rail_lengths import EARTH_RADIUS, haversine

DEFAULT_STATIONS = "./json/railway_stations.json"
DEFAULT_SEGMENTS = "./csv/rail_segments.csv"

# Velikost buňky prostorového indexu ve stupních
CELL = 0.01
DEFAULT_MAX_DISTANCE = 60.0

Point = tuple[float, float]
Segment = tuple[Point, Point]


def cell_of(lat: float, lon: float) -> tuple[int, int]:
    return math.floor(lat / CELL), math.floor(lon / CELL)


def segment_of(row: list[str]) -> Segment:
    """Neorientovaný úsek z řádku CSV (každý je v CSV dvakrát, tam a zpět)."""
    a = (float(row[0]), float(row[1]))
    b = (float(row[2]), float(row[3]))
    return min(a, b), max(a, b)


def build_index(segments: list[Segment]) -> dict[tuple[int, int], list[int]]:
    """Každý úsek se zařadí do všech buněk, které pokrývá jeho obdélník."""
    index = defaultdict(list)
    for i, (a, b) in enumerate(segments):
        lat1, lon1 = cell_of(min(a[0], b[0]), min(a[1], b[1]))
        lat2, lon2 = cell_of(max(a[0], b[0]), max(a[1], b[1]))
        for la in range(lat1, lat2 + 1):
            for lo in range(lon1, lon2 + 1):
                index[(la, lo)].append(i)
    return index


def point_segment_distance(p: Point, a: Point, b: Point) -> tuple[float, float]:
    """Vzdálenost bodu od úsečky v metrech a poloha průmětu na ní (0 = a, 1 = b).

    Počítá se v lokální ekvidistantní projekci kolem bodu p.
    """
    scale = math.radians(1) * EARTH_RADIUS
    cos_lat = math.cos(math.radians(p[0]))
    ax, ay = (a[1] - p[1]) * cos_lat * scale, (a[0] - p[0]) * scale
    bx, by = (b[1] - p[1]) * cos_lat * scale, (b[0] - p[0]) * scale
    dx, dy = bx - ax, by - ay
    length2 = dx * dx + dy * dy
    t = 0.0 if length2 == 0 else max(0.0, min(1.0, -(ax * dx + ay * dy) / length2))
    return math.hypot(ax + t * dx, ay + t * dy), t


def segments_within(p: Point, segments: list[Segment], index: dict, max_distance: float) -> list[tuple[int, float, float]]:
    """Všechny úseky do vzdálenosti max_distance od bodu jako (index úseku, vzdálenost, poloha průmětu)."""
    # Obdélník kolem bodu, který pokrývá kruh o poloměru max_distance
    scale = math.radians(1) * EARTH_RADIUS
    dlat = max_distance / scale
    dlon = max_distance / (scale * math.cos(math.radians(p[0])))
    lat1, lon1 = cell_of(p[0] - dlat, p[1] - dlon)
    lat2, lon2 = cell_of(p[0] + dlat, p[1] + dlon)
    found = []
    seen = set()
    for la in range(lat1, lat2 + 1):
        for lo in range(lon1, lon2 + 1):
            for i in index.get((la, lo), ()):
                if i in seen:
                    continue
                seen.add(i)
                distance, t = point_segment_distance(p, *segments[i])
                if distance <= max_distance:
                    found.append((i, distance, t))
    return found


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-s", "--stations", type=Path, default=DEFAULT_STATIONS, help="Input JSON file with stations")
    parser.add_argument("-i", "--input", type=Path, default=DEFAULT_SEGMENTS, help="CSV file with rail segments (rewritten)")
    parser.add_argument(
        "--max-distance",
        type=float,
        default=DEFAULT_MAX_DISTANCE,
        help=f"Link the station to all segments within this distance in meters (default {DEFAULT_MAX_DISTANCE:.0f})",
    )
    args = parser.parse_args()

    stations = json.loads(args.stations.read_text(encoding="utf-8")).get("elements", [])
    with args.input.open(encoding="utf-8", newline="") as file:
        reader = csv.reader(file)
        header = next(reader)
        rows = list(reader)
    segments = list({segment_of(row) for row in rows})
    index = build_index(segments)

    # Stanice promítnuté dovnitř úseku: index úseku -> [(poloha na úseku, stanice)]
    splits = defaultdict(list)
    # Stanice, jejichž nejbližším místem je koncový bod úseku
    endpoint_links = []
    # Napojené stanice: (vzdálenost nejbližšího úseku, počet úseků, jméno)
    matches = []
    on_track = too_far = 0
    for station in stations:
        p = (station["lat"], station["lon"])
        name = station.get("tags", {}).get("name", station["id"])
        found = segments_within(p, segments, index, args.max_distance)
        if not found:
            too_far += 1
            print(f"Skipped {name}: no segment within {args.max_distance:.0f} m", file=sys.stderr)
            continue
        # Stanice ležící přímo na bodu kolejí už v grafu je, napojí se jen na ostatní koleje v okolí
        if any(p in segments[i] for i, _, _ in found):
            on_track += 1
        linked = 0
        for i, distance, t in found:
            a, b = segments[i]
            if p in (a, b):
                continue
            linked += 1
            if t == 0.0:
                endpoint_links.append((a, p))
            elif t == 1.0:
                endpoint_links.append((p, b))
            else:
                splits[i].append((t, p))
        if linked:
            matches.append((min(distance for _, distance, _ in found), linked, name))

    # Rozdělený úsek se nahradí řetězcem A - stanice ... - B seřazeným podle polohy na úseku
    removed = set()
    new_segments = {}
    for i, points in splits.items():
        a, b = segments[i]
        chain = [a, *(p for _, p in sorted(points)), b]
        removed.add(segments[i])
        new_segments.update(dict.fromkeys(zip(chain, chain[1:])))
    new_segments.update(dict.fromkeys(endpoint_links))

    written = 0
    with args.input.open("w", encoding="utf-8", newline="") as file:
        writer = csv.writer(file, lineterminator="\n")
        writer.writerow(header)
        for row in rows:
            if segment_of(row) not in removed:
                writer.writerow(row)
        for a, b in new_segments:
            if a == b:
                continue
            distance = round(haversine(*a, *b), 2)
            writer.writerow([*a, *b, distance])
            writer.writerow([*b, *a, distance])
            written += 2

    print("Farthest stations from their nearest segment:", file=sys.stderr)
    for distance, _, name in sorted(matches, reverse=True)[:10]:
        print(f"  {distance:8.1f} m  {name}", file=sys.stderr)
    print("Stations linked to the most segments:", file=sys.stderr)
    for _, linked, name in sorted(matches, key=lambda m: m[1], reverse=True)[:10]:
        print(f"  {linked:8d}    {name}", file=sys.stderr)
    print(
        f"{len(matches)} stations linked to {sum(m[1] for m in matches)} segments "
        f"({len(removed)} segments split, {len(endpoint_links)} links to an end point), "
        f"{written} records added into {args.input}, {on_track} already on track, {too_far} skipped",
        file=sys.stderr,
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
