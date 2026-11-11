import {OrderOverview, OrderOverviewResponseDTO} from 'src/app/order/order';

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
    const cargoTypes = order.cargoes?.map(cargo => cargoTypesMap[cargo.cargoTypeId]).filter(Boolean) || [];

    // Vytvoření konečného objektu kombinací původního OrderDTO a nalezených entit
    return {
      ...order,
      stationFrom,
      stationTo,
      trainSet,
      cargoTypes
    };
  });
}
