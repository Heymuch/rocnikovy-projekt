import { Routes } from '@angular/router';
import { Home } from './home/home';
import {MyOrders} from './my-orders/my-orders';
import {NewOrder} from './new-order/new-order';
import {TrainOverview} from './train-overview/train-overview';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'my-orders', component: MyOrders },
  { path: 'new-order', component: NewOrder },
  {path: 'train-overview', component: TrainOverview},
  { path: '**', redirectTo: '' },
];
