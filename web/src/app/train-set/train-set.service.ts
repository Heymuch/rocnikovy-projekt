
import {Injectable} from '@angular/core'
import {ApiService} from 'src/app/commons/api.service'
import {Observable, retry} from 'rxjs'
import {TrainSetOverviewResponseDTO,
  TrainSetDetailResponseDTO} from 'src/app/train-set/train-set'
import {HttpHeaders, HttpParams} from '@angular/common/http'

export type TrainSetApiIncludes =
  'station' |
  'locomotive' |
  'wagon' |
  'railwayNode' |
  'cargoType'

export interface TrainSetPageRequest {
  pageNumber: number
  pageSize: number
  includes?: TrainSetApiIncludes[]
}

@Injectable({
  providedIn: 'root'
})
export class TrainSetsService extends ApiService {
  private readonly endpoint = `${this.apiUrl}/train-sets`

  searchTrainSets(request: TrainSetPageRequest): Observable<TrainSetOverviewResponseDTO> {

    const params = new HttpParams()
      .set('page', request.pageNumber)
      .set('size', request.pageSize)

    let headers = new HttpHeaders()

    if (request.includes && request.includes.length > 0) {
      headers = headers.set('X-Include', request.includes.join(','))
    }

    return this.http.get<TrainSetOverviewResponseDTO>(`${this.endpoint}/search`, {
      params,
      headers
    }).pipe(
      retry(1)
    )
  }

  getTrainSetDetail(
    id: string,
    includes: TrainSetApiIncludes[] =
    ['locomotive', 'wagon', 'station', 'cargoType', 'railwayNode']
  ): Observable<TrainSetDetailResponseDTO> {

    let headers = new HttpHeaders()

    if (includes.length > 0) {
      headers = headers.set('X-Include', includes.join(','))
    }

    return this.http.get<TrainSetDetailResponseDTO>(
      `${this.endpoint}/${id}`,
      {headers}
    ).pipe(
      retry(1)
    )
  }
}
