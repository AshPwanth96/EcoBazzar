import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seller-dashboard.html'
})
export class SellerDashboard implements OnInit {
  private productSvc = inject(ProductService);
  private router = inject(Router);

  products: any[] = [];
  loading = false;
  error: string | null = null;

  stats = { total: 0, certified: 0, requested: 0 };

  ngOnInit(): void { this.load(); }

  load() {
    this.loading = true;
    this.productSvc.getSellerProducts().subscribe({
      next: (res:any) => {
        this.products = res || [];
        this.stats.total = this.products.length;
        this.stats.certified = this.products.filter(p => p.ecoCertified).length;
        this.stats.requested = this.products.filter(p => !p.ecoCertified && p.ecoRequested).length;
        this.loading = false;
      },
      error: (err) => { console.error(err); this.error = 'Failed to load seller products'; this.loading = false; }
    });
  }

  goAdd() { this.router.navigate(['/seller/product']); }

  edit(p:any) {
    this.router.navigate([`/seller/product`], { state: { product: p } }); // or open a modal
  }

  deleteProduct(id:number) {
    if (!confirm('Delete this product?')) return;
    this.productSvc.delete(id).subscribe({ next: ()=> this.load(), error: ()=> alert('Delete failed') });
  }
}
