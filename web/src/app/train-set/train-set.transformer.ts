import {
  TrainSetOverview,
  TrainSetOverviewResponseDTO,
  TrainSetDetail,
  TrainSetDetailResponseDTO,
  TrainSetOrderDetail
} from 'src/app/train-set/train-set';

import {StationDTO} from 'src/app/station/station';
import {
  transformRoutePoints
} from 'src/app/commons/route-map/route-map.transformer';

export function transformTrainSetsResponse(
  response: TrainSetOverviewResponseDTO
): TrainSetOverview[] {

  // Ochrana před chybějícími daty
  if (!response || !response.items) {
    return [];
  }

  // Příprava mapy stanic podle jejich ID
  const stationsMap = response.extend?.stations?.reduce((acc, station) => {
    acc[station.id] = station;
    return acc;
  }, {} as Record<string, StationDTO>) || {};

  // Přiřazení stanic k jednotlivým vlakům
  return response.items.map(trainSet => {

    const stationFrom = stationsMap[trainSet.stationFromId] ?? null;
    const stationTo = stationsMap[trainSet.stationToId] ?? null;

    return {
      ...trainSet,
      stationFrom,
      stationTo
    };
  });
}

export function transformTrainSetDetailResponse(
  response: TrainSetDetailResponseDTO
): TrainSetDetail {

  // Mapa stanic podle ID
  const stationsMap = response.extend?.stations?.reduce(
    (acc, station) => {
      acc[station.id] = station;
      return acc;
    },
    {} as Record<string, StationDTO>
  ) || {};

  // Mapa typů zboží podle ID
  const cargoTypesMap = new Map(
    response.extend?.cargoTypes?.map(cargoType => [
      cargoType.id,
      cargoType
    ]) ?? []
  );

  // Transformace přiřazených objednávek
  const orders: TrainSetOrderDetail[] =
    response.orders.map(({order, user}) => ({
      ...order,
      user,

      stationFrom: order.route?.stationFrom
        ? stationsMap[order.route.stationFrom] ?? null
        : null,

      stationTo: order.route?.stationTo
        ? stationsMap[order.route.stationTo] ?? null
        : null,

      cargoType: order.cargo
        ? cargoTypesMap.get(order.cargo.cargoTypeId) ?? null
        : null
    }));

  // Transformace souřadnic železniční trasy
  const routePoints = transformRoutePoints(
    response.route,
    response.extend?.railwayNodes
  );

  return {
    ...response,

    stationFrom: response.stationFromId
      ? stationsMap[response.stationFromId] ?? null
      : null,

    stationTo: response.stationToId
      ? stationsMap[response.stationToId] ?? null
      : null,

    locomotives: response.extend?.locomotives ?? [],
    wagons: response.extend?.wagons ?? [],
    orders,
    routePoints
  };
}
