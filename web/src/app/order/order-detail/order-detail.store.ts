import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {
  patchState,
  signalStore,
  withMethods,
  withState
} from '@ngrx/signals';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {HttpErrorResponse} from '@angular/common/http';
import {tapResponse} from '@ngrx/operators';
import {exhaustMap, pipe, switchMap, tap} from 'rxjs';

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
  isDeleting: boolean;
  deleteError: boolean;
}

interface OrderDetailRequest {
  id: string;
  isAdmin: boolean;
}

const initialState: OrderDetailState = {
  order: null,
  isLoading: false,
  error: false,
  isDeleting: false,
  deleteError: false
};

export const OrderDetailStore = signalStore(
  withState(initialState),

  withMethods((
    state,
    ordersService = inject(OrdersService),
    router = inject(Router)
  ) => ({

    loadOrder: rxMethod<OrderDetailRequest>(
      pipe(
        tap(() => patchState(state, {
          order: null,
          isLoading: true,
          error: false,
          deleteError: false
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
    ),

    deleteOrder: rxMethod<string>(
      pipe(
        exhaustMap((id) => {

          patchState(state, {
            isDeleting: true,
            deleteError: false
          });

          return ordersService.deleteOrder(id).pipe(
            tapResponse({
              next: () => {
                patchState(state, {
                  isDeleting: false
                });

                void router.navigate(['/my-orders']);
              },

              error: (err: HttpErrorResponse | Error) => {
                console.error(
                  'Chyba při rušení objednávky:',
                  err
                );

                patchState(state, {
                  isDeleting: false,
                  deleteError: true
                });
              }
            })
          );
        })
      )
    )

  }))
);
