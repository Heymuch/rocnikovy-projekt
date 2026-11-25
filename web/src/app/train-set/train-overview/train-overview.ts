import { Component, inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

import { TrainTable } from 'src/app/train-set/train-table/train-table';
import { TrainsOverviewStore } from 'src/app/train-set/trains-overview.store';

@Component({
  selector: 'app-train-overview',
  imports: [TrainTable, TranslatePipe],
  templateUrl: './train-overview.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './train-overview.scss',
})
export class TrainOverview implements OnInit {
  readonly store = inject(TrainsOverviewStore);
  readonly trains = this.store.trains;

  ngOnInit(): void {
    // TODO: Stránkování později přesunout do samostatného ovládání
    this.store.loadTrains({
      pageNumber: 0,
      pageSize: 25,
      includes: ['station'],
    });
  }
}
