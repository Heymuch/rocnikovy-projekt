import { Routes } from '@angular/router';
import { Home } from './home/home';
import {MyOrders} from 'src/app/order/my-orders/my-orders';
import {NewOrder} from 'src/app/order/new-order/new-order';
import {TrainOverview} from 'src/app/train-set/train-overview/train-overview';
import {OrderDetail} from 'src/app/order/order-detail/order-detail';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'my-orders', component: MyOrders },
  { path: 'my-orders/:id', component: OrderDetail },
  { path: 'new-order', component: NewOrder },
  {path: 'train-overview', component: TrainOverview},
  { path: '**', redirectTo: '' },
];
