import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product';
import { ReportService } from '../../services/report.service';
import { Router, RouterModule } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seller-dashboard.html'
})
export class SellerDashboard implements OnInit {

  private productSvc = inject(ProductService);
  private reportSvc = inject(ReportService);
  private router = inject(Router);

  products: any[] = [];
  loading = false;
  error: string | null = null;

  stats = {
    total: 0,
    certified: 0,
    requested: 0,
    orders: 0,
    revenue: 0
  };

  badge: string | null = null;
  private chart: any;

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = null;

    const pProducts = this.productSvc.getSellerProducts().pipe(catchError(() => of([])));
    const pReport = this.reportSvc.getSellerReport().pipe(catchError(() => of(null)));
    const pSales = this.reportSvc.getSellerSales(14).pipe(catchError(() => of([])));

    forkJoin([pProducts, pReport, pSales]).subscribe({
      next: ([productsRes, reportRes, salesRes]: any) => {
        this.products = productsRes || [];
        this.stats.total = this.products.length;
        this.stats.certified = this.products.filter((p:any) => p.ecoCertified).length;
        this.stats.requested = this.products.filter((p:any) => !p.ecoCertified && p.ecoRequested).length;

        this.stats.orders = Number(reportRes?.totalOrders ?? 0);
        this.stats.revenue = Number(reportRes?.totalRevenue ?? 0);
        this.badge = reportRes?.ecoSellerBadge ?? reportRes?.sellerName ?? '—';

        this.renderChart(salesRes || []);
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load seller dashboard';
        this.loading = false;
      }
    });
  }

  renderChart(data: any[]) {
    const labels = data.map(d => d.day);
    const values = data.map(d => d.revenue);
    const ctx = document.getElementById('salesChart') as HTMLCanvasElement;
    if (!ctx) return;
    if (this.chart) this.chart.destroy();
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Revenue',
          data: values,
          tension: 0.3,
          fill: true,
          borderWidth: 2
        }]
      },
      options: {
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    });
  }

  goAdd() { this.router.navigate(['/seller/product']); }
  edit(p: any) { this.router.navigate(['/seller/product'], { state: { product: p } }); }
  deleteProduct(id: number) {
    if (!confirm('Delete this product?')) return;
    this.productSvc.delete(id).subscribe({ next: () => this.load(), error: () => alert('Delete failed') });
  }
}
