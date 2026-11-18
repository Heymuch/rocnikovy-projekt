
import {
  TrainSetOverview,
  TrainSetOverviewResponseDTO
} from 'src/app/train-set/train-set';

import {StationDTO} from 'src/app/station/station';

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
