import { Component, OnInit, AfterViewInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product';
import { ReportService } from '../../services/report.service';
import { Router, RouterModule } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seller-dashboard.html'
})
export class SellerDashboard implements OnInit, AfterViewInit, OnDestroy {
  private productSvc = inject(ProductService);
  private reportSvc = inject(ReportService);
  private router = inject(Router);

  products: any[] = [];
  loading = true;
  error: string | null = null;

  stats = {
    total: 0,
    certified: 0,
    requested: 0,
    orders: 0,
    revenue: 0
  };

  badge: string | null = null;
  private chart: Chart | null = null;

  ngOnInit(): void {
    this.load();
  }

  ngAfterViewInit(): void {
    // nothing here - chart is created after data arrives in load()
  }

  ngOnDestroy(): void {
    if (this.chart) this.chart.destroy();
  }

  load() {
    this.loading = true;
    this.error = null;

    const products$ = this.productSvc.getSellerProducts().pipe(catchError(() => of([])));
    const report$ = this.reportSvc.getSellerReport().pipe(catchError(() => of(null)));
    const sales$ = this.reportSvc.getSellerSales(14).pipe(catchError(() => of([])));

    forkJoin([products$, report$, sales$]).subscribe({
      next: ([productsRes, reportRes, salesRes]: any) => {
        // debug prints help while developing — remove later if noisy
        // console.log('seller products', productsRes);
        // console.log('seller report', reportRes);
        // console.log('seller sales raw', salesRes);

        this.products = productsRes || [];
        this.stats.total = this.products.length;
        this.stats.certified = this.products.filter((p: any) => p.ecoCertified).length;
        this.stats.requested = this.products.filter((p: any) => p.ecoRequested && !p.ecoCertified).length;

        this.stats.orders = Number(reportRes?.totalOrders ?? 0);
        this.stats.revenue = Number(reportRes?.totalRevenue ?? 0);
        // keep backward-compatible badge key if backend uses different name
        this.badge = reportRes?.ecoSellerBadge ?? reportRes?.badge ?? 'New Seller';

        // Render chart with robust handler (handles empty, single point, string numbers etc.)
        this.renderChart(salesRes || []);
        this.loading = false;
      },
      error: (err) => {
        console.error('Seller dashboard load error:', err);
        this.error = 'Failed to load dashboard. Please try again.';
        this.loading = false;
      }
    });
  }

  renderChart(data: any[]) {
    // guard
    const canvas = document.getElementById('salesChart') as HTMLCanvasElement | null;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // ensure array
    const rows = Array.isArray(data) ? data : [];

    // Defensive: normalize rows into day/revenue pairs.
    // Backend shape examples we accept: { day: 'YYYY-MM-DD', revenue: 150 } or { date: '...', value: ... }
    const normalized = rows.map((r: any) => {
      const day = r.day ?? r.date ?? r.label ?? '';
      const v = r.revenue ?? r.value ?? r.amount ?? 0;
      const n = typeof v === 'number' ? v : Number(v);
      return { day: String(day), revenue: Number.isFinite(n) ? n : 0 };
    });

    const labels = normalized.length > 0 ? normalized.map(r => r.day) : ['No data'];
    const values = normalized.length > 0 ? normalized.map(r => r.revenue) : [0];

    if (this.chart) this.chart.destroy();

    // If only a single point, make it visible (bigger point, optional vertical padding)
    const pointRadius = values.length === 1 ? 6 : 4;
    const suggestedMax = Math.max(...values, 0) * 1.15 || undefined;

    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: 'Revenue (₹)',
          data: values,
          borderColor: '#10b981',
          backgroundColor: 'rgba(16,185,129,0.12)',
          tension: 0.2,
          fill: true,
          spanGaps: true,
          pointBackgroundColor: '#10b981',
          pointRadius,
          pointHoverRadius: pointRadius + 2,
          showLine: values.length > 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { mode: 'index', intersect: false }
        },
        scales: {
          y: {
            beginAtZero: true,
            suggestedMax,
            grid: { display: false }
          },
          x: {
            grid: { display: false },
            ticks: {
              autoSkip: labels.length > 10,
              maxRotation: 0,
              minRotation: 0
            }
          }
        }
      }
    });
  }

  goAdd() {
    this.router.navigate(['/seller/product']);
  }

  edit(product: any) {
    this.router.navigate(['/seller/product'], { state: { product } });
  }

  deleteProduct(id: number) {
    if (!confirm('Are you sure you want to delete this product?')) return;

    this.productSvc.delete(id).subscribe({
      next: () => {
        this.products = this.products.filter(p => p.id !== id);
        this.stats.total--;
        this.showToast('Product deleted successfully');
        this.load(); // Refresh stats and chart
      },
      error: () => this.showToast('Failed to delete product', 'error')
    });
  }

  showToast(message: string, type: 'success' | 'error' = 'success') {
    const toast = document.createElement('div');
    toast.textContent = message;
    toast.className = `fixed bottom-8 right-8 px-8 py-4 rounded-xl shadow-2xl text-white font-bold z-50 transition-all ${
      type === 'success' ? 'bg-gradient-to-r from-green-600 to-emerald-600' : 'bg-red-600'
    }`;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('opacity-0'), 2500);
    setTimeout(() => toast.remove(), 3000);
  }
}
