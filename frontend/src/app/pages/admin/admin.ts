import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

interface PlatformReport {
  totalOrders: number;
  totalRevenue: number;
  totalCarbonUsed: number;
  totalCarbonSaved: number;
  netCarbon: number;
  totalUsers: number;
  totalProducts: number;
}

interface PendingProduct {
  id: number;
  name: string;
  price: number;
  carbonImpact: number;
  sellerName: string;
}

interface PendingSeller {
  id: number;
  name: string;
  email: string;
  productCount: number;
}

interface PendingAdminRequest {
  id: number;
  user: { id: number; name: string; email: string };
  requestedAt: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  providers: [DatePipe],
  templateUrl: './admin.html',
  styleUrls: ['./admin.scss']
})
export class Admin implements OnInit {
  private http = inject(HttpClient);
  private datePipe = inject(DatePipe);

  report: PlatformReport | null = null;
  pendingProducts: PendingProduct[] = [];
  pendingSellers: PendingSeller[] = [];
  pendingAdminRequests: PendingAdminRequest[] = [];

  loading = true;
  processing = new Set<number>(); // Prevents double clicks

  get today(): string {
    return this.datePipe.transform(new Date(), 'mediumDate') || '';
  }

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.loading = true;

    this.http.get<PlatformReport>('/api/admin/reports').subscribe({
      next: (r) => this.report = r,
      error: () => this.showToast('Failed to load stats', 'error')
    });

    this.loadPendingProducts();
    this.loadPendingSellers();
    this.loadPendingAdminRequests();

    setTimeout(() => this.loading = false, 600);
  }

  private loadPendingProducts(): void {
    this.http.get<PendingProduct[]>('/api/admin/pending-products').subscribe({
      next: (d) => this.pendingProducts = d,
      error: () => this.showToast('Failed to load products', 'error')
    });
  }

  private loadPendingSellers(): void {
    this.http.get<PendingSeller[]>('/api/admin/pending-sellers').subscribe({
      next: (d) => this.pendingSellers = d,
      error: () => this.showToast('Failed to load sellers', 'error')
    });
  }

  private loadPendingAdminRequests(): void {
    this.http.get<PendingAdminRequest[]>('/api/admin-request/pending').subscribe({
      next: (d) => this.pendingAdminRequests = d,
      error: () => this.showToast('Failed to load admin requests', 'error')
    });
  }

  approveProduct(id: number): void {
    if (this.processing.has(id)) return;
    this.processing.add(id);

    this.http.put(`/api/admin/approveProduct/${id}`, {}).subscribe({
      next: () => {
        this.showToast('Eco Product Certified!');
        this.loadAllData();
      },
      error: () => this.showToast('Failed', 'error'),
      complete: () => this.processing.delete(id)
    });
  }

  rejectProduct(id: number): void {
    if (this.processing.has(id)) return;
    if (!confirm('Reject this product? This will clear the eco-request.')) return;

    this.processing.add(id);
    this.http.put(`/api/admin/rejectProduct/${id}`, {}).subscribe({
      next: () => {
        this.showToast('Product request rejected', 'success');
        this.loadAllData();
      },
      error: () => this.showToast('Failed to reject', 'error'),
      complete: () => this.processing.delete(id)
    });
  }

  approveSeller(id: number): void {
    if (this.processing.has(id)) return;
    this.processing.add(id);

    this.http.put(`/api/admin/approveSeller/${id}`, {}).subscribe({
      next: () => {
        this.showToast('Seller Approved!');
        this.loadAllData();
      },
      error: () => this.showToast('Failed', 'error'),
      complete: () => this.processing.delete(id)
    });
  }

  approveAdminRequest(id: number): void {
    if (this.processing.has(id)) return;
    this.processing.add(id);

    this.http.post(`/api/admin-request/approve/${id}`, {}).subscribe({
      next: () => {
        this.showToast('New Admin Added!');
        this.loadAllData();
      },
      error: () => this.showToast('Failed', 'error'),
      complete: () => this.processing.delete(id)
    });
  }

  rejectAdminRequest(id: number): void {
    if (this.processing.has(id)) return;
    this.processing.add(id);

    this.http.post(`/api/admin-request/reject/${id}`, {}).subscribe({
      next: () => {
        this.showToast('Request Rejected');
        this.loadAllData();
      },
      error: () => this.showToast('Failed', 'error'),
      complete: () => this.processing.delete(id)
    });
  }

  downloadCsv(): void {
    const token = localStorage.getItem('token'); // or get from your AuthService
    if (!token) {
      this.showToast('Not authenticated', 'error');
      return;
    }

    this.http.get('/api/admin/reports/export', {
      responseType: 'blob',
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'orders-report.csv';
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
        this.showToast('CSV downloaded');
      },
      error: (err) => {
        console.error('CSV download failed', err);
        this.showToast('Download failed', 'error');
      }
    });
  }

  showToast(message: string, type: 'success' | 'error' = 'success'): void {
    const toast = document.createElement('div');
    toast.textContent = message;
    toast.className = `fixed bottom-8 right-8 px-8 py-5 rounded-2xl shadow-2xl text-white font-bold text-lg z-50 transition-all duration-500
      ${type === 'success' ? 'bg-gradient-to-r from-green-600 to-emerald-600' : 'bg-gradient-to-r from-red-600 to-rose-600'}`;

    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('translate-y-16', 'opacity-0'), 2500);
    setTimeout(() => toast.remove(), 3000);
  }
}
