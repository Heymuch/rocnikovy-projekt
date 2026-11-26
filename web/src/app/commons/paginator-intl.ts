import {
  Injectable,
  DestroyRef,
  inject
} from '@angular/core';

import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Injectable()
export class PaginatorIntl extends MatPaginatorIntl {

  private readonly translate = inject(TranslateService);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    super();

    this.translate.stream('pagination')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.translateLabels();
      });
  }

  private translateLabels(): void {

    this.itemsPerPageLabel =
      this.translate.instant('pagination.itemsPerPage');

    this.nextPageLabel =
      this.translate.instant('pagination.nextPage');

    this.previousPageLabel =
      this.translate.instant('pagination.previousPage');

    this.firstPageLabel =
      this.translate.instant('pagination.firstPage');

    this.lastPageLabel =
      this.translate.instant('pagination.lastPage');

    this.changes.next();
  }

  override getRangeLabel = (
    page: number,
    pageSize: number,
    length: number
  ): string => {

    const of = this.translate.instant('pagination.of');

    if (length === 0 || pageSize === 0) {
      return `0 ${of} ${length}`;
    }

    const startIndex = page * pageSize;
    const endIndex = Math.min(startIndex + pageSize, length);

    return `${startIndex + 1} – ${endIndex} ${of} ${length}`;
  };
}
