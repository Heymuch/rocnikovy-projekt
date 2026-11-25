import { Routes } from '@angular/router';
import { Home } from './home/home';
import {MyOrders} from 'src/app/order/my-orders/my-orders';
import {NewOrder} from 'src/app/order/new-order/new-order';
import {TrainOverview} from 'src/app/train-set/train-overview/train-overview';
import {OrderDetail} from 'src/app/order/order-detail/order-detail';
import {TrainDetail} from 'src/app/train-set/train-detail/train-detail';
import { roleGuard } from 'src/app/commons/auth/auth.guard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'my-orders', component: MyOrders, canActivate: [roleGuard], data: { roles: ['customer', 'admin'] } }, // admin, user - ale posílají se různé věci admin/user, zjistit, jestli se to dělá BE/FE
  { path: 'my-orders/:id', component: OrderDetail, canActivate: [roleGuard], data: { roles: ['customer', 'admin'] } },  // admin, user -||-
  { path: 'new-order', component: NewOrder, canActivate: [roleGuard], data: { roles: ['customer'] }}, // customer
  {path: 'train-overview', component: TrainOverview, canActivate: [roleGuard], data: { roles: ['admin'] }}, //jen admin
  {path: 'train-overview/:id', component: TrainDetail, canActivate: [roleGuard], data: { roles: ['admin'] }}, // admin
  { path: '**', redirectTo: '' },
];
