import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CartService {

  private base = 'http:localhost:8080/api/cart'

  constructor(private http:HttpClient){}

  add(productId:number, quantity:number = 1):Observable<any>{

    return this.http.post(this.base, {productId, quantity});
  }

  getSummary():Observable<any>{
    return this.http.get(this.base)

  }

  remove(itemId:number):Observable<any>{
    return this.http.delete(`${this.base}/${itemId}`);  
  }
  
}
