import { Routes } from '@angular/router';
import { Home } from './home/home';
import {MyOrders} from './my-orders/my-orders';
import {NewOrder} from './new-order/new-order';
import {OrderDetail} from './order-detail/order-detail';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'my-orders', component: MyOrders },
  { path: 'my-orders/:id', component: OrderDetail },
  { path: 'new-order', component: NewOrder },
  { path: '**', redirectTo: '' },
];
