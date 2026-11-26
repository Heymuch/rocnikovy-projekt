import {
  Component,
  inject,
  OnInit,
  ChangeDetectionStrategy
} from '@angular/core';

import { TranslatePipe } from '@ngx-translate/core';

import { TrainTable } from 'src/app/train-set/train-table/train-table';
import { TrainsOverviewStore } from 'src/app/train-set/trains-overview.store';

import {
  MatPaginatorModule,
  PageEvent
} from '@angular/material/paginator';

import { PaginatedTableDirective } from 'src/app/commons/directives/paginated-table.directive';

@Component({
  selector: 'app-train-overview',
  imports: [
    TrainTable,
    TranslatePipe,
    MatPaginatorModule,
    PaginatedTableDirective
  ],
  templateUrl: './train-overview.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './train-overview.scss',
})
export class TrainOverview implements OnInit {

  readonly store = inject(TrainsOverviewStore);
  readonly trains = this.store.trains;

  ngOnInit(): void {
    this.store.loadTrains({
      pageNumber: this.store.page(),
      pageSize: this.store.size()
    });
  }

  onPageChange(event: PageEvent): void {
    this.store.changePage(
      event.pageIndex,
      event.pageSize
    );
  }
}
