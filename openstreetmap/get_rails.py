#!/usr/bin/env python3
import argparse
import json
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

# Veřejné Overpass servery, zkouší se postupně při selhání předchozího
# https://wiki.openstreetmap.org/wiki/Overpass_API#Public_Overpass_API_instances
SERVERS = [
    "https://overpass-api.de/api/interpreter",
    "https://overpass.private.coffee/api/interpreter",
]

# railway=rail (normální rozchod) a railway=narrow_gauge (úzkorozchodné tratě)
# na území ČR, bez vleček, kolejišť, odstavných a manipulačních kolejí
# (tag service=*). Každá cesta (way) obsahuje tagy a geometrii (seznam souřadnic).
QUERY = """
[out:json][timeout:300];
area["ISO3166-1"="CZ"][admin_level=2]->.cz;
way["railway"~"^(rail|narrow_gauge)$"]
   ["service"!~"."]
   (area.cz);
out geom tags;
"""

DEFAULT_OUTPUT = "./json/railway_rails.json"
USER_AGENT = "upce-rocnikovy-projekt/1.0 (railway rails export)"


def fetch(server: str, query: str) -> dict:
    data = urllib.parse.urlencode({"data": query}).encode()
    request = urllib.request.Request(server, data=data, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(request, timeout=360) as response:
        return json.load(response)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-o", "--output", type=Path, default=DEFAULT_OUTPUT, help="Output JSON file")
    args = parser.parse_args()

    for server in SERVERS:
        print(f"Calling {server} ...", file=sys.stderr)
        try:
            result = fetch(server, QUERY)
            break
        except (urllib.error.URLError, TimeoutError, json.JSONDecodeError) as e:
            print(f"Failed: {e}", file=sys.stderr)
    else:
        print("No response", file=sys.stderr)
        return 1

    args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"{len(result.get('elements', []))} records were saved into {args.output}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
