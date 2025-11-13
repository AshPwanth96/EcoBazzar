import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product';
import { ProductService } from '../../services/product';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, FormsModule, RouterModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss',
})
export class ProductList  implements OnInit{

  products: Product[] = []
  filtered: Product[] = []
  searchText = '';
  ecoOnly = false;
  loading = false;
  error:string|null=null;

  constructor(private productService: ProductService){}

  ngOnInit(): void {
      this.loadProducts();
  }

  loadProducts():void{
    this.loading = true;
    this.productService.getAll().subscribe({
      next:(data)=>{
        this.products= data;
        this.filtered = data;
        this.loading = false;
      },
      error:()=> {
        this.error='Could not Load Products';
        this.loading = false;
      },
    })
  }
  applyFilters():void{
    const text = this.searchText.toLowerCase().trim();
    this.filtered = this.products.filter(p=>{
      const matchSearch = !text||p.name?.toLowerCase().includes(text);
      const matchEco = !this.ecoOnly||p.ecoCertified;
      return matchEco&&matchSearch;
    })
  }

  onSearchChange():void{this.applyFilters();}
  onEcoToggle():void{this.applyFilters();}
}
