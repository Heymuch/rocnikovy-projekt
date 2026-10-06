#!/usr/bin/env python3
"""Calculate approximate length of every railway rail.

Every rail in railway_rails.json gets a new attribute "length" (in meters).
The length is the sum of great-circle distances between consecutive points
of the rail geometry. Rails without geometry fall back to the diagonal
of their bounding box.
"""
import argparse
import json
import math
import sys
from pathlib import Path

DEFAULT_RAILS = "./json/railway_rails.json"

EARTH_RADIUS = 6371008.8


def haversine(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """Vzdálenost dvou bodů po povrchu Země v metrech."""
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = phi2 - phi1
    dlambda = math.radians(lon2 - lon1)
    a = math.sin(dphi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlambda / 2) ** 2
    return 2 * EARTH_RADIUS * math.asin(math.sqrt(a))


def rail_length(rail: dict) -> float | None:
    geometry = rail.get("geometry") or []
    if len(geometry) >= 2:
        return sum(haversine(a["lat"], a["lon"], b["lat"], b["lon"]) for a, b in zip(geometry, geometry[1:]))

    # Bez geometrie zbývá jen úhlopříčka bounding boxu (horní odhad přímé vzdálenosti konců)
    bounds = rail.get("bounds")
    if bounds:
        return haversine(bounds["minlat"], bounds["minlon"], bounds["maxlat"], bounds["maxlon"])
    return None
