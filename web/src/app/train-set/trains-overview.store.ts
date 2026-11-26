
import {inject} from '@angular/core';
import {pipe, switchMap, tap} from 'rxjs';
import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {HttpErrorResponse} from '@angular/common/http';
import {tapResponse} from '@ngrx/operators';

import {
  TrainSetOverview,
  TrainSetOverviewResponseDTO
} from 'src/app/train-set/train-set';

import {
  TrainSetPageRequest,
  TrainSetsService
} from 'src/app/train-set/train-set.service';

import {transformTrainSetsResponse} from 'src/app/train-set/train-set.transformer';

interface TrainsOverviewState {
  trains: TrainSetOverview[];
  isLoading: boolean;
  error: boolean;
  page: number;
  size: number;
  totalCount: number;
}

const initialState: TrainsOverviewState = {
  trains: [],
  isLoading: false,
  error: false,
  page: 0,
  size: 10,
  totalCount: 0
};

export const TrainsOverviewStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),
  withMethods((state, trainSetsService = inject(TrainSetsService)) => ({

    loadTrains: rxMethod<TrainSetPageRequest>(
      pipe(
        tap((request) => patchState(state, {
          isLoading: true,
          error: false,
          page: request.pageNumber,
          size: request.pageSize
        })),

        switchMap((request) =>
          trainSetsService.searchTrainSets(request).pipe(
            tapResponse({
              next: (response: TrainSetOverviewResponseDTO) => {
                const transformedTrains = transformTrainSetsResponse(response);

                patchState(state, {
                  trains: transformedTrains,
                  page: response.page,
                  size: response.size,
                  totalCount: response.totalCount,
                  isLoading: false
                });
              },

              error: (err: HttpErrorResponse | Error) => {
                console.error('Chyba při načítání vlaků:', err);

                patchState(state, {
                  trains: [],
                  totalCount: 0,
                  isLoading: false,
                  error: true
                });
              }
            })
          )
        )
      )
    ),

    changePage(pageNumber: number, pageSize: number) {
      this.loadTrains({
        pageNumber,
        pageSize
      });
    }

  }))
);
