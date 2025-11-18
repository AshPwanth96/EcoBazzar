import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CartService } from '../../services/cart';
import { OrderService } from '../../services/order.service';
import { ProductService } from '../../services/product';
import { Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, RouterLink],
  templateUrl: './cart.html',
})
export class Cart implements OnInit {

  private cartService = inject(CartService);
  private orderService = inject(OrderService);
  private productService = inject(ProductService);
  private router = inject(Router);

  items: any[] = [];
  totalPrice = 0;
  totalCarbon = 0;             // shows total carbon used (kg)
  totalCarbonSaved = 0;       // optional, if you want to show saved too
  ecoSuggestion: string | null = null;

  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart() {
    this.loading = true;
    this.error = null;

    this.cartService.getSummary().subscribe({
      next: (res: any) => {
        // Backend returns totalCarbonUsed / totalCarbonSaved
        this.totalPrice = res.totalPrice ?? 0;
        this.totalCarbon = res.totalCarbonUsed ?? 0;
        this.totalCarbonSaved = res.totalCarbonSaved ?? 0;
        this.ecoSuggestion = res.ecoSuggestion ?? null;

        const items = res.items || [];
        if (!items || items.length === 0) {
          this.items = [];
          this.loading = false;
          return;
        }

        // Build array of product fetch observables
        const calls = items.map((item: any) =>
          this.productService.getById(item.productId).pipe(
            catchError(err => {
              console.error('Failed to load product', item.productId, err);
              // return a safe fallback product object so UI still renders
              return of({
                id: item.productId,
                name: item.productName || 'Unknown product',
                price: item.price || 0,
                carbonImpact: item.carbonImpact || 0,
                imageUrl: item.imageUrl || null
              });
            })
          )
        );

        // Run all product calls in parallel
        (forkJoin(calls) as any).subscribe({
          next: (productsAny: any) => {
            const products: any[] = productsAny as any[];

            // Merge the product data back into cart items in the original order
            this.items = items.map((it: any, idx: number) => {
              const p = products[idx] || {};
              return {
                ...it,
                productName: p.name || it.productName,
                price: p.price ?? it.price,
                carbonImpact: p.carbonImpact ?? it.carbonImpact,
                imageUrl: p.imageUrl ?? it.imageUrl
              };
            });
            this.loading = false;
          },
          error: (err: any) => {
            console.error('Failed to load cart products', err);
            this.error = 'Failed to load cart products';
            this.items = items; // fallback to raw items (minimal)
            this.loading = false;
          }
        });
      },
      error: (err: any) => {
        console.error('Failed to load cart summary', err);
        this.error = "❌ Failed to load cart";
        this.items = [];
        this.loading = false;
      }
    });
  }

  remove(id: number) {
    if (!confirm('Remove this item?')) return;
    this.cartService.remove(id).subscribe({
      next: () => this.loadCart(),
      error: (err: any) => {
        console.error('Remove failed', err);
        alert('❌ Remove failed');
      }
    });
  }

  checkout() {
    this.orderService.checkout().subscribe({
      next: () => {
        alert('✅ Order placed successfully!');
        this.router.navigate(['/dashboard']);
      },
      error: (err: any) => {
        console.error('Checkout error full:', err);
        const status = err?.status ?? 'unknown';
        const serverBody = (err?.error && typeof err.error !== 'string') ? JSON.stringify(err.error) : (err?.error || err?.message || 'No message');
        alert(`❌ Checkout failed (status: ${status})\nServer response: ${serverBody}`);
      }
    });
  }
}
