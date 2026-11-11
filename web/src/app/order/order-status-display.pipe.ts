import { Pipe, PipeTransform } from '@angular/core';
import { OrderStatus } from 'src/app/order/order-status';

@Pipe({
  name: 'orderStatusDisplay',
  standalone: true
})
export class OrderStatusDisplayPipe implements PipeTransform {
  transform(value: string | OrderStatus): string {
    if (!value) return '';

    const statusMap: Record<string, string> = {
      [OrderStatus.IN_QUEUE]: 'orderStatuses.inQueue',
      [OrderStatus.CONFIRMED]: 'orderStatuses.confirmed',
      [OrderStatus.IN_TRANSIT]: 'orderStatuses.inTransit',
      [OrderStatus.COMPLETED]: 'orderStatuses.completed'
    };

    return statusMap[value as string] || value as string;
  }
}
