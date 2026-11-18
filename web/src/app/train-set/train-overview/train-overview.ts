
import { Component, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { TrainTable } from '../train-table/train-table';

// Datové typy podle backendu
export interface TrainSet {
  id: string;
  code: string;
  locomotiveId: number;
  stationFromId: string;
  stationToId: string;
  departureDate: string;
  arrivalDate: string;
  expirationDate: string;
}

export interface Station {
  id: string;
  name: string;
}

interface TrainOverviewResponse {
  items: TrainSet[];
  extend: {
    stations: Station[];
  };
  page: number;
  size: number;
  totalCount: number;
  totalPages: number;
}

@Component({
  selector: 'app-train-overview',
  standalone: true,
  imports: [TranslatePipe, TrainTable],
  templateUrl: './train-overview.html',
  styleUrl: './train-overview.scss'
})
export class TrainOverview {
  readonly isLoading = signal(false);

  readonly trainOverview = signal<TrainOverviewResponse>({
    items: [
      {
        id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
        code: 'TRAIN-MAIN-001',
        locomotiveId: 1001,
        stationFromId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
        stationToId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3',
        departureDate: '2026-11-15T07:00:00Z',
        arrivalDate: '2026-11-15T13:00:00Z',
        expirationDate: '2026-11-15T15:00:00Z'
      },
      {
        id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2',
        code: 'TRAIN-LOCAL-002',
        locomotiveId: 1002,
        stationFromId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4',
        stationToId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
        departureDate: '2026-12-10T08:00:00Z',
        arrivalDate: '2026-12-10T10:00:00Z',
        expirationDate: '2026-12-10T11:00:00Z'
      }
    ],
    extend: {
      stations: [
        {
          id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
          name: 'Praha hl.n.'
        },
        {
          id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3',
          name: 'Ostrava hl.n.'
        },
        {
          id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa4',
          name: 'Plzeň hl.n.'
        }
      ]
    },
    page: 0,
    size: 25,
    totalCount: 2,
    totalPages: 1
  });
}
