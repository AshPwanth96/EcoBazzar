import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserReportService } from '../../services/user-report';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {

  private reportSvc: UserReportService = inject(UserReportService);

  name: string | null = localStorage.getItem('name');
  role: string | null = localStorage.getItem('role');

  loading = false;
  error: string | null = null;
  report: any = null;

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport() {
    this.loading = true;

    this.reportSvc.getReport().subscribe({
      next: (res: any) => {

        // SAFE rounding (avoid undefined errors)
        res.totalCarbonUsed = Number((res.totalCarbonUsed || 0).toFixed(2));
        res.totalCarbonSaved = Number((res.totalCarbonSaved || 0).toFixed(2));

        this.report = res;
        this.loading = false;
      },
      error: () => {
        this.error = "Failed to load eco report";
        this.loading = false;
      }
    });
  }

  getBadgeColor(): string {
    if (!this.report) return '';

    const badge = this.report.ecoBadge;

    if (badge.includes("Eco Legend")) return "from-yellow-500 to-amber-500";
    if (badge.includes("Green Hero")) return "from-green-600 to-green-500";
    if (badge.includes("Conscious Shopper")) return "from-blue-600 to-blue-500";
    if (badge.includes("Beginner")) return "from-lime-600 to-lime-500";

    return "from-gray-500 to-gray-400";
  }
}
