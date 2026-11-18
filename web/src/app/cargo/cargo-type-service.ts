import { ApiService } from 'src/app/commons/api.service';
import { Injectable } from '@angular/core';
import { map, Observable, retry } from 'rxjs';
import { CargoTypeDTO, CargoTypesResponseDTO } from 'src/app/cargo/cargo';



@Injectable({
  providedIn: 'root'
})
export class CargoTypeService extends ApiService {
  private readonly endpoint = `${this.apiUrl}/cargo-types`;

  getAll(): Observable<CargoTypeDTO[]> {
    return this.http.get<CargoTypesResponseDTO>(`${this.endpoint}/all`).pipe(
      retry(1),
      map((response) => response.items),
    );
  }
}
