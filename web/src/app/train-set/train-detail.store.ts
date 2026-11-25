import {inject} from '@angular/core';
import {pipe, switchMap, tap} from 'rxjs';
import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {HttpErrorResponse} from '@angular/common/http';
import {tapResponse} from '@ngrx/operators';

import {
  TrainSetDetail,
  TrainSetDetailResponseDTO
} from 'src/app/train-set/train-set';

import {TrainSetsService} from 'src/app/train-set/train-set.service';

import {
  transformTrainSetDetailResponse
} from 'src/app/train-set/train-set.transformer';

interface TrainDetailState {
  train: TrainSetDetail | null;
  isLoading: boolean;
  error: boolean;
}

const initialState: TrainDetailState = {
  train: null,
  isLoading: false,
  error: false
};

export const TrainDetailStore = signalStore(
  withState(initialState),

  withMethods((state, trainSetsService = inject(TrainSetsService)) => ({

    loadTrain: rxMethod<string>(
      pipe(
        tap(() => patchState(state, {
          train: null,
          isLoading: true,
          error: false
        })),

        switchMap((id) =>
          trainSetsService.getTrainSetDetail(id).pipe(
            tapResponse({
              next: (response: TrainSetDetailResponseDTO) => {
                const transformedTrain =
                  transformTrainSetDetailResponse(response);

                patchState(state, {
                  train: transformedTrain,
                  isLoading: false
                });
              },

              error: (err: HttpErrorResponse | Error) => {
                console.error('Chyba při načítání detailu vlaku:', err);

                patchState(state, {
                  train: null,
                  isLoading: false,
                  error: true
                });
              }
            })
          )
        )
      )
    )

  }))
);
