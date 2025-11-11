import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/model.product';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private base = 'http://localhost:8080/api/products'; // backend API

  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.base);
  }
}
