import {
  RailwayNodeDTO,
  RoutePoint
} from 'src/app/commons/route-map/route-map.model';

export function transformRoutePoints(
  routeIds?: string[] | null,
  railwayNodes?: RailwayNodeDTO[] | null
): RoutePoint[] {

  if (!routeIds?.length || !railwayNodes?.length) {
    return [];
  }

  const nodesMap = new Map(
    railwayNodes.map(node => [node.id, node])
  );

  const routePoints: RoutePoint[] = [];

  for (const id of routeIds) {
    const node = nodesMap.get(id);

    if (!node ||
      !Number.isFinite(node.latitude) ||
      !Number.isFinite(node.longitude) ||
      Math.abs(node.latitude) > 90 ||
      Math.abs(node.longitude) > 180) {
      return [];
    }

    routePoints.push({
      latitude: node.latitude,
      longitude: node.longitude
    });
  }

  return routePoints;
}
