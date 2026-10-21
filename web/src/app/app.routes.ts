import { Routes } from '@angular/router';
import { Home } from './home/home';
import {MyOrders} from './my-orders/my-orders';
import {NewOrder} from './new-order/new-order';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'my-orders', component: MyOrders },
  { path: 'new-order', component: NewOrder },
  { path: '**', redirectTo: '' },
];
