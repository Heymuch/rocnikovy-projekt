import argparse
import csv
from pathlib import Path
import sys
import uuid


DEFAULT_INPUT = "./csv/segments.csv"
DEFAULT_OUTPUT = "./sql/segments.sql"

UUID_NAMESPACE = uuid.NIL

def get_uuid_v5(point: tuple[float, float]) -> uuid.UUID:
    PRECISION = 10_000_000
    return uuid.uuid5(UUID_NAMESPACE, f"{int(point[0] * PRECISION)},{int(point[1] * PRECISION)}")

def row_to_sql(row: list[str]) -> str:
    point_a_id = get_uuid_v5((float(row[0]), float(row[1])))
    point_b_id = get_uuid_v5((float(row[2]), float(row[3])))
    return f"""INSERT INTO railway_graph(point_a_id, point_a_latitude, point_a_longitude point_b_id, point_b_latitude, point_b_longitude distance)
        VALUE ('{str(point_a_id)}', {float(row[0])}, {float(row[1])}, '{str(point_b_id)}', {float(row[2])}, {float(row[3])}, {round(float(row[4]))});
    """


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-i", "--input", type=Path, default=DEFAULT_INPUT, help="Input CSV file with segments")
    parser.add_argument("-o", "--output", type=Path, default=DEFAULT_OUTPUT, help="Output SQL file")
    args = parser.parse_args()

    with args.input.open(encoding="utf-8", newline="") as file:
        reader = csv.reader(file)
        header = next(reader)
        rows = list(reader)

    output = "\n".join([row_to_sql(row) for row in rows])
    args.output.write_text(output)

if __name__ == "__main__":
    sys.exit(main())
