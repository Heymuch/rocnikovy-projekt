import {Injectable} from '@angular/core'
import {ApiService} from 'src/app/commons/api.service'
import {Observable, retry} from 'rxjs'
import {OrderOverviewResponseDTO} from 'src/app/order/order'
import {HttpHeaders, HttpParams} from '@angular/common/http'

export type ApiIncludes = 'trainSet' | 'cargoType' | 'station'

export interface OrderPageRequest {
  pageNumber: number
  pageSize: number
  userId?: string
  includes?: ApiIncludes[]
}

@Injectable({
  providedIn: 'root'
})
export class OrdersService extends ApiService {
  private readonly endpoint = `${this.apiUrl}/orders`

  searchOrders(request: OrderPageRequest): Observable<OrderOverviewResponseDTO> {

    let params = new HttpParams()
      .set('page', request.pageNumber)
      .set('size', request.pageSize)

    if (request.userId) {
      params = params.set('userId', request.userId)
    }

    let headers = new HttpHeaders()

    if (request.includes && request.includes.length > 0) {
      headers = headers.set('X-Include', request.includes.join(','))
    }

    return this.http.get<OrderOverviewResponseDTO>(`${this.endpoint}/search`, {
      params,
      headers
    }).pipe(
      retry(1)
    )
  }
}
