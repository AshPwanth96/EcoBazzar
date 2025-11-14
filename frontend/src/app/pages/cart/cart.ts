import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CartService } from '../../services/cart';
import { OrderService } from '../../services/order.service';
import { ProductService } from '../../services/product';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  templateUrl: './cart.html',
})
export class Cart implements OnInit {

  private cartService = inject(CartService);
  private orderService = inject(OrderService);
  private productService = inject(ProductService);
  private router = inject(Router);

  items: any[] = [];
  totalPrice = 0;
  totalCarbon = 0;
  ecoSuggestion: string | null = null;

  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart() {
    this.loading = true;

    this.cartService.getSummary().subscribe({
      next: (res: any) => {
        this.totalPrice = res.totalPrice;
        this.totalCarbon = res.totalCarbon;
        this.ecoSuggestion = res.ecoSuggestion;

        if (!res.items || res.items.length === 0) {
          this.items = [];
          this.loading = false;
          return;
        }

        const enrichedItems: any[] = [];
        let completed = 0;

        res.items.forEach((item: any) => {
          this.productService.getById(item.productId).subscribe({
            next: (product: any) => {
              enrichedItems.push({
                ...item,
                productName: product.name,
                price: product.price,
                carbonImpact: product.carbonImpact,
                imageUrl: product.imageUrl,
              });

              completed++;
              if (completed === res.items.length) {
                this.items = enrichedItems;
                this.loading = false;
              }
            },
            error: () => {
              enrichedItems.push(item);
              completed++;
            }
          });
        });
      },

      error: () => {
        this.error = "❌ Failed to load cart";
        this.loading = false;
      }
    });
  }

  remove(id: number) {
    if (!confirm('Remove this item?')) return;
    this.cartService.remove(id).subscribe(() => this.loadCart());
  }

  checkout() {
    this.orderService.checkout().subscribe({
      next: () => {
        alert('✅ Order placed successfully!');
        this.router.navigate(['/dashboard']);
      },
      error: () => alert('❌ Checkout failed')
    });
  }
}
