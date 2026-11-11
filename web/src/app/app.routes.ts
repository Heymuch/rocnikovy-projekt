import { Routes } from '@angular/router';
import { Home } from './home/home';
import {MyOrders} from './my-orders/my-orders';
import {NewOrder} from './new-order/new-order';
import {TrainOverview} from './train-overview/train-overview';
import {OrderDetail} from './order-detail/order-detail';
import {TrainDetail} from 'src/app/train-detail/train-detail';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'my-orders', component: MyOrders },
  { path: 'my-orders/:id', component: OrderDetail },
  { path: 'new-order', component: NewOrder },
  {path: 'train-overview', component: TrainOverview},
  {path: 'train-overview/:id', component: TrainDetail},
  { path: '**', redirectTo: '' },
];
