#!/usr/bin/env python3
"""Find the shortest rail path between two railway stations.

Reads stations from railway_stations.json and the rail graph from
rail_segments.csv (edges between GPS points weighted by distance in meters).
A station is a graph node identified by its GPS location. Stations can be
given by name or OSM node id (e.g. "Cheb", "node/3036772660"); when omitted,
the user is asked for them interactively.

Prints the "from" station, the "to" station, the distance and the stations
passed on the way.
"""
import argparse
import csv
import heapq
import json
import sys
from collections import defaultdict
from pathlib import Path

DEFAULT_STATIONS = "./json/railway_stations.json"
DEFAULT_SEGMENTS = "./csv/rail_segments.csv"

Point = tuple[float, float]


def load_stations(path: Path) -> list[dict]:
    elements = json.loads(path.read_text(encoding="utf-8")).get("elements", [])
    return [
        {"id": e["id"], "name": e.get("tags", {}).get("name", f"node/{e['id']}"), "point": (e["lat"], e["lon"])}
        for e in elements
        if "lat" in e and "lon" in e
    ]


def load_graph(path: Path) -> dict[Point, list[tuple[Point, float]]]:
    graph = defaultdict(list)
    with path.open(encoding="utf-8", newline="") as file:
        for row in csv.DictReader(file):
            a = (float(row["lat_of_a"]), float(row["lon_of_a"]))
            b = (float(row["lat_of_b"]), float(row["lon_of_b"]))
            graph[a].append((b, float(row["distance_in_meters"])))
    return graph


def find_station(stations: list[dict], query: str) -> dict | list[dict]:
    """Return the matching station, or a list of candidates when ambiguous or not found."""
    query = query.strip()
    node_id = query.removeprefix("node/")
    if node_id.isdigit():
        for station in stations:
            if station["id"] == int(node_id):
                return station
        return []
    folded = query.casefold()
    exact = [s for s in stations if s["name"].casefold() == folded]
    if len(exact) == 1:
        return exact[0]
    return exact or [s for s in stations if folded in s["name"].casefold()]


def pick_station(stations: list[dict], query: str | None, label: str) -> dict:
    interactive = query is None
    while True:
        if interactive:
            try:
                query = input(f"{label}: ")
            except EOFError:
                sys.exit(1)
        result = find_station(stations, query)
        if isinstance(result, dict):
            return result
        if not result:
            print(f"Station '{query}' not found.", file=sys.stderr)
        else:
            print(f"Station '{query}' is ambiguous, candidates:", file=sys.stderr)
            for station in sorted(result, key=lambda s: s["name"])[:20]:
                print(f"  {station['name']} (node/{station['id']})", file=sys.stderr)
            if len(result) > 20:
                print(f"  ... and {len(result) - 20} more", file=sys.stderr)
        if not interactive:
            sys.exit(1)


def shortest_path(graph: dict, start: Point, goal: Point) -> tuple[float, list[Point]] | None:
    """Dijkstra's algorithm, returns the distance and the points of the path."""
    distances = {start: 0.0}
    previous = {}
    queue = [(0.0, start)]
    while queue:
        distance, point = heapq.heappop(queue)
        if point == goal:
            path = [point]
            while point in previous:
                point = previous[point]
                path.append(point)
            return distance, path[::-1]
        # Zastaralý záznam ve frontě, bod už byl zpracován s kratší vzdáleností
        if distance > distances[point]:
            continue
        for neighbour, weight in graph.get(point, ()):
            candidate = distance + weight
            if candidate < distances.get(neighbour, float("inf")):
                distances[neighbour] = candidate
                previous[neighbour] = point
                heapq.heappush(queue, (candidate, neighbour))
    return None


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--from", dest="source", help="Start station (name or node/<id>)")
    parser.add_argument("--to", dest="target", help="Destination station (name or node/<id>)")
    parser.add_argument("--stations", type=Path, default=DEFAULT_STATIONS, help="Input JSON file with stations")
    parser.add_argument("--segments", type=Path, default=DEFAULT_SEGMENTS, help="Input CSV file with rail segments")
    args = parser.parse_args()

    stations = load_stations(args.stations)
    source = pick_station(stations, args.source, "From")
    target = pick_station(stations, args.target, "To")

    graph = load_graph(args.segments)
    for station in (source, target):
        if station["point"] not in graph:
            print(f"Station '{station['name']}' is not connected to any rail segment.", file=sys.stderr)
            return 1

    result = shortest_path(graph, source["point"], target["point"])
    print(f"From:     {source['name']} (node/{source['id']})")
    print(f"To:       {target['name']} (node/{target['id']})")
    if result is None:
        print("No path exists between the stations.")
        return 1

    distance, path = result
    print(f"Distance: {distance / 1000:.2f} km")

    # Na jednom bodě může ležet více stanic, proto seznam
    by_point = defaultdict(list)
    for station in stations:
        by_point[station["point"]].append(station)
    via = [s for point in path[1:-1] for s in by_point.get(point, ())]
    if via:
        print("Via:")
        for station in via:
            print(f"  {station['name']} (node/{station['id']})")
    return 0


if __name__ == "__main__":
    sys.exit(main())
