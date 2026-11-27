import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable, retry } from 'rxjs';

import { ApiService } from 'src/app/commons/api.service';
import {
  PaginatedStationResponseDTO
} from 'src/app/station/station';

export interface StationPageRequest {
  pageNumber: number;
  pageSize: number;
  query?: string;
}

@Injectable({
  providedIn: 'root'
})
export class StationService extends ApiService {

  private readonly endpoint = `${this.apiUrl}/stations`;

  searchStations(
    request: StationPageRequest
  ): Observable<PaginatedStationResponseDTO> {

    let params = new HttpParams()
      .set('page', request.pageNumber)
      .set('size', request.pageSize);

    if (request.query?.trim()) {
      params = params.set('query', request.query.trim());
    }

    return this.http.get<PaginatedStationResponseDTO>(
      `${this.endpoint}/search`,
      { params }
    ).pipe(
      retry(1)
    );
  }
}
