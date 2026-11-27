import {
  OrderOverview,
  OrderOverviewResponseDTO,
  OrderDetail,
  OrderDetailResponseDTO
} from 'src/app/order/order';

import {
  transformRoutePoints
} from 'src/app/commons/route-map/route-map.transformer';

export function transformOrdersResponse(response: OrderOverviewResponseDTO): OrderOverview[] {
  // Ochrana před chybějícími daty
  if (!response || !response.items) {
    return [];
  }

  // Příprava "slovníků" (map) pro rychlé vyhledávání z pole extend
  // Pomocí reduce si z pole vytvoříme objekt, kde klíčem je ID a hodnotou samotný objekt
  const stationsMap = response.extend?.stations?.reduce((acc, station) => {
    acc[station.id] = station;
    return acc;
  }, {} as Record<string, any>) || {};

  const trainSetsMap = response.extend?.trainSets?.reduce((acc, trainSet) => {
    acc[trainSet.id] = trainSet;
    return acc;
  }, {} as Record<string, any>) || {};

  const cargoTypesMap = response.extend?.cargoTypes?.reduce((acc, cargoType) => {
    acc[cargoType.id] = cargoType;
    return acc;
  }, {} as Record<number, any>) || {};

  // Samotné mapování
  return response.items.map(order => {
    // Nalezení stanic z mapy podle UUID z order.route
    const stationFrom = order.route?.stationFrom ? stationsMap[order.route.stationFrom] : null;
    const stationTo = order.route?.stationTo ? stationsMap[order.route.stationTo] : null;

    // Nalezení vlaku podle trainSetId
    const trainSet = order.trainSetId ? trainSetsMap[order.trainSetId] : null;

    // Nalezení typů nákladu. Protože jedna objednávka může mít více položek nákladu (cargoes),
    // namapujeme je všechny.
    const cargoType = order.cargo ? cargoTypesMap[order.cargo.cargoTypeId] : null;

    // Vytvoření konečného objektu kombinací původního OrderDTO a nalezených entit
    return {
      ...order,
      stationFrom,
      stationTo,
      trainSet,
      cargoType
    };
  });
}

export function transformOrderDetailResponse(
  response: OrderDetailResponseDTO
): OrderDetail {

  const {order, extend} = response;

  const stationFrom = extend?.stations?.find(
    station => station.id === order.route?.stationFrom
  ) ?? null;

  const stationTo = extend?.stations?.find(
    station => station.id === order.route?.stationTo
  ) ?? null;

  const trainSet = extend?.trainSets?.find(
    train => train.id === order.trainSetId
  ) ?? null;

  const cargoType = extend?.cargoTypes?.find(
    cargoType => cargoType.id === order.cargo?.cargoTypeId
  ) ?? null;

  const wagon = extend?.wagons?.find(
    wagon => wagon.id === order.cargo?.wagonId
  ) ?? null;

  const user = extend?.users?.find(
    user => user.id === order.userId
  ) ?? null;

  const routePoints = transformRoutePoints(
    order.route?.route,
    extend?.railwayNodes
  );

  return {
    ...order,
    stationFrom,
    stationTo,
    trainSet,
    cargoType,
    wagon,
    user,
    routeLengthKm: response.routeLengthKm ?? null,
    routePoints,
    statusHistory: [...(response.statusHistory ?? [])]
      .sort((a, b) =>
        new Date(a.changedAt).getTime() -
        new Date(b.changedAt).getTime()
      )
  };
}
