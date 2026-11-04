import argparse
import csv
from pathlib import Path
import sys
import uuid


DEFAULT_INPUT = "./csv/segments.csv"
DEFAULT_OUTPUT_FOR_NODES = "./sql/railway_nodes.sql"
DEFAULT_OUTPUT_FOR_GRAPH = "./sql/railway_graph.sql"

UUID_NAMESPACE = uuid.NIL

def get_uuid_v5(point: tuple[float, float]) -> uuid.UUID:
    PRECISION = 10_000_000
    return uuid.uuid5(UUID_NAMESPACE, f"{int(point[0] * PRECISION)},{int(point[1] * PRECISION)}")

def railway_node_to_sql(node) -> str:
    return f"""INSERT INTO railway_nodes(id, latitude, longitude) VALUES ('{str(node["id"])}', {node["latitude"]}, {node["longitude"]});"""

def railway_graph_to_sql(record) -> str:
    return f"""INSERT INTO railway_graph(node_a_id, node_b_id, distance) VALUES ('{str(record["node_a"])}', '{str(record["node_b"])}', {record["distance"]});"""

def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-i", "--input", type=Path, default=DEFAULT_INPUT, help="Input CSV file with segments")
    args = parser.parse_args()

    with args.input.open(encoding="utf-8", newline="") as file:
        reader = csv.reader(file)
        header = next(reader)
        rows = list(reader)

    railway_nodes = {}
    railway_graph = []
    for row in rows:
        node_a = { "id": get_uuid_v5((float(row[0]), float(row[1]))), "latitude": float(row[0]), "longitude": float(row[1]) }
        node_b = { "id": get_uuid_v5((float(row[2]), float(row[3]))), "latitude": float(row[2]), "longitude": float(row[3]) }
        railway_nodes[node_a["id"]] = node_a
        railway_nodes[node_b["id"]] = node_b
        railway_graph.append({ "node_a": node_a["id"], "node_b": node_b["id"], "distance": round(float(row[4])) })

    railway_nodes_sql = "\n".join([railway_node_to_sql(node[1]) for node in railway_nodes.items()])
    railway_graph_sql = "\n".join([railway_graph_to_sql(record) for record in railway_graph])

    Path(DEFAULT_OUTPUT_FOR_NODES).write_text(railway_nodes_sql)
    Path(DEFAULT_OUTPUT_FOR_GRAPH).write_text(railway_graph_sql)

if __name__ == "__main__":
    sys.exit(main())
