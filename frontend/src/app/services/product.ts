import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private base = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  // Get all products
  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.base);
  }

  getById(id:number):Observable<Product>{
    return this.http.get<Product>(`${this.base}/${id}`);
  }
}
