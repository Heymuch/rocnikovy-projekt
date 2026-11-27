import {inject} from '@angular/core';
import {pipe, switchMap, tap} from 'rxjs';
import {
  patchState,
  signalStore,
  withMethods,
  withState
} from '@ngrx/signals';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {HttpErrorResponse} from '@angular/common/http';
import {tapResponse} from '@ngrx/operators';

import {
  OrderDetail,
  OrderDetailResponseDTO
} from 'src/app/order/order';

import {
  ApiIncludes,
  OrdersService
} from 'src/app/order/order.service';

import {
  transformOrderDetailResponse
} from 'src/app/order/order.transformer';

interface OrderDetailState {
  order: OrderDetail | null;
  isLoading: boolean;
  error: boolean;
}

interface OrderDetailRequest {
  id: string;
  isAdmin: boolean;
}

const initialState: OrderDetailState = {
  order: null,
  isLoading: false,
  error: false
};

export const OrderDetailStore = signalStore(
  withState(initialState),

  withMethods((state, ordersService = inject(OrdersService)) => ({

    loadOrder: rxMethod<OrderDetailRequest>(
      pipe(
        tap(() => patchState(state, {
          order: null,
          isLoading: true,
          error: false
        })),

        switchMap(({id, isAdmin}) => {

          const includes: ApiIncludes[] = [
            'trainSet',
            'cargoType',
            'station',
            'wagon',
            'railwayNode'
          ];

          if (isAdmin) {
            includes.push('user');
          }

          return ordersService.getOrderDetail(id, includes).pipe(
            tapResponse({
              next: (response: OrderDetailResponseDTO) => {

                const transformedOrder =
                  transformOrderDetailResponse(response);

                patchState(state, {
                  order: transformedOrder,
                  isLoading: false
                });
              },

              error: (err: HttpErrorResponse | Error) => {
                console.error(
                  'Chyba při načítání detailu objednávky:',
                  err
                );

                patchState(state, {
                  order: null,
                  isLoading: false,
                  error: true
                });
              }
            })
          );
        })
      )
    )
  }))
);
