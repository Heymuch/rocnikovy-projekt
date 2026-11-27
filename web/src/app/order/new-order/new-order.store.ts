import { inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { exhaustMap, pipe, switchMap, tap } from 'rxjs';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { tapResponse } from '@ngrx/operators';

import {
  OrderCalculateRequestDTO,
  OrderCalculateResponseDTO,
  OrderCalculation,
  OrderCreateRequestDTO,
  OrderCreateResponseDTO,
} from 'src/app/order/order';
import { OrdersService } from 'src/app/order/order.service';
import { transformOrderCalculationResponse } from 'src/app/order/order.transformer';

export type NewOrderStep = 'form' | 'summary';

interface NewOrderState {
  step: NewOrderStep;
  calculation: OrderCalculation | null;
  isCalculating: boolean;
  isSubmitting: boolean;
  error: string | null;
}

const initialState: NewOrderState = {
  step: 'form',
  calculation: null,
  isCalculating: false,
  isSubmitting: false,
  error: null,
};

export const NewOrderStore = signalStore(
  withState(initialState),

  withMethods(
    (
      state,
      ordersService = inject(OrdersService),
      router = inject(Router),
    ) => ({
      calculateOrder: rxMethod<OrderCalculateRequestDTO>(
        pipe(
          tap(() =>
            patchState(state, {
              isCalculating: true,
              error: null,
            }),
          ),

          switchMap((request) =>
            ordersService.calculateOrder(request).pipe(
              tapResponse({
                next: (response: OrderCalculateResponseDTO) => {
                  const calculation = transformOrderCalculationResponse(response);

                  patchState(state, {
                    calculation,
                    step: 'summary',
                    isCalculating: false,
                  });
                },

                error: (err: HttpErrorResponse | Error) => {
                  console.error('Chyba při výpočtu objednávky:', err);

                  patchState(state, {
                    isCalculating: false,
                    error: 'newOrder.calculateError',
                  });
                },
              }),
            ),
          ),
        ),
      ),

      createOrder: rxMethod<OrderCreateRequestDTO>(
        pipe(
          tap(() =>
            patchState(state, {
              isSubmitting: true,
              error: null,
            }),
          ),

          exhaustMap((request) =>
            ordersService.createOrder(request).pipe(
              tapResponse({
                next: (response: OrderCreateResponseDTO) => {
                  patchState(state, {
                    isSubmitting: false,
                  });

                  router.navigate(['/my-orders', response.order.id]);
                },

                error: (err: HttpErrorResponse | Error) => {
                  console.error('Chyba při vytváření objednávky:', err);

                  patchState(state, {
                    isSubmitting: false,
                    error: 'newOrder.createError',
                  });
                },
              }),
            ),
          ),
        ),
      ),

      backToForm(): void {
        patchState(state, {
          step: 'form',
          error: null,
        });
      },
    }),
  ),
);
