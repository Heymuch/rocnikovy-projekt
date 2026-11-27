import { inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { pipe, switchMap, tap } from 'rxjs';

import {
  patchState,
  signalStore,
  withState,
  withComputed,
  withMethods
} from '@ngrx/signals';

import { computed } from '@angular/core';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { tapResponse } from '@ngrx/operators';

import {
  StationDTO,
  PaginatedStationResponseDTO
} from 'src/app/station/station';

import {
  StationService,
  StationPageRequest
} from 'src/app/station/station.service';


interface StationComboboxState {
  stations: StationDTO[];
  isLoading: boolean;
  error: boolean;
  query: string;
  page: number;
  size: number;
  totalCount: number;
  totalPages: number;
}

const initialState: StationComboboxState = {
  stations: [],
  isLoading: false,
  error: false,
  query: '',
  page: 0,
  size: 20,
  totalCount: 0,
  totalPages: 0,
};

export const StationComboboxStore = signalStore(

  withState(initialState),

  withComputed((state) => ({

    hasMore: computed(() =>
      state.page() + 1 < state.totalPages()
    )

  })),

  withMethods((state, stationService = inject(StationService)) => ({

    loadStations: rxMethod<StationPageRequest>(
      pipe(

        tap((request) => {

          patchState(state, {
            isLoading: true,
            error: false,
            query: request.query ?? ''
          });

          if (request.pageNumber === 0) {
            patchState(state, {
              stations: [],
              page: 0,
              totalCount: 0,
              totalPages: 0
            });
          }

        }),

        switchMap((request) =>
          stationService.searchStations(request).pipe(

            tapResponse({

              next: (response: PaginatedStationResponseDTO) => {

                const stations = request.pageNumber === 0
                  ? response.items
                  : [...state.stations(), ...response.items];

                patchState(state, {
                  stations,
                  page: response.page,
                  size: response.size,
                  totalCount: response.totalCount,
                  totalPages: response.totalPages,
                  isLoading: false
                });

              },

              error: (err: HttpErrorResponse | Error) => {

                console.error(
                  'Chyba při načítání stanic:',
                  err
                );

                patchState(state, {
                  isLoading: false,
                  error: true
                });

              }

            })

          )
        )

      )
    ),

    searchStations(query: string) {

      this.loadStations({
        pageNumber: 0,
        pageSize: state.size(),
        query
      });

    },

    loadMore() {

      if (state.isLoading() || !state.hasMore()) {
        return;
      }

      this.loadStations({
        pageNumber: state.page() + 1,
        pageSize: state.size(),
        query: state.query()
      });

    }

  }))

);
