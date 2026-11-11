import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export abstract class ApiService {
  protected http = inject(HttpClient);
  protected apiUrl = '/api';
}
