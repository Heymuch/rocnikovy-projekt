import { inject } from '@angular/core';
import { switchMap, tap } from 'rxjs';
import { pipe } from 'rxjs';
import {OrderOverview, OrderOverviewResponseDTO} from 'src/app/order/order';
import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {OrderPageRequest, OrdersService} from 'src/app/order/order.service';
import {transformOrdersResponse} from 'src/app/order/order.transformer';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import {HttpErrorResponse} from '@angular/common/http';
import {tapResponse} from '@ngrx/operators';

interface OrdersOverviewState {
  orders: OrderOverview[];
  isLoading: boolean;
  page: number;
  size: number;
  totalCount: number;
}

const initialState: OrdersOverviewState = {
  orders: [],
  isLoading: false,
  page: 0,
  size: 25,
  totalCount: 0,
};

export const OrdersOverviewStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((state, ordersService = inject(OrdersService)) => ({

    loadOrders: rxMethod<OrderPageRequest>(
      pipe(
        tap((request) => patchState(state, { isLoading: true, page: request.pageNumber, size: request.pageSize })),

        switchMap((request) =>
          ordersService.searchOrders(request).pipe(
            tapResponse({
              next: (response: OrderOverviewResponseDTO) => {
                const transformedOrders = transformOrdersResponse(response);
                patchState(state, {
                  orders: transformedOrders,
                  totalCount: response.totalCount,
                  isLoading: false,
                });
              },
              error: (err: HttpErrorResponse | Error) => {
                console.error('Chyba při načítání objednávek:', err);
                patchState(state, { isLoading: false });
              },
            })
          )
        )
      )
    ),

    // Čistá pomocná metoda, pokud chceš změnit stránku přímo z komponenty
    changePage(pageNumber: number) {
      this.loadOrders({ pageNumber, pageSize: state.size(), includes: ['trainSet', 'cargoType', 'station'] });
    }
  }))
)
