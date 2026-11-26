import { Directive } from '@angular/core';

@Directive({
  selector: '[appPaginatedTable]',
  standalone: true,
  host: {
    'class': 'paginated-table'
  }
})
export class PaginatedTableDirective {
}
