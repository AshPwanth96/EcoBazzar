import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserReportService } from '../../services/user-report';

import { catchError, finalize, of } from 'rxjs';

// ⭐ Pure Chart.js
import Chart from 'chart.js/auto';

export interface UserReport {
  userId: number;
  userName: string;
  totalPurchase: number;
  totalSpent: number;
  totalCarbonUsed: number;
  totalCarbonSaved: number;
  ecoBadge: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class Dashboard implements OnInit {

  private reportSvc = inject(UserReportService);

  name = localStorage.getItem('name') ?? 'User';
  role = localStorage.getItem('role') ?? 'GUEST';

  loading = false;
  error: string | null = null;
  report: UserReport | null = null;

  // Two charts
  chartSaved!: Chart;
  chartUsed!: Chart;

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport() {
    this.loading = true;

    this.reportSvc.getReport()
      .pipe(
        catchError(err => {
          console.error(err);
          this.error = 'Failed to load eco report';
          return of(null);
        }),
        finalize(() => this.loading = false)
      )
      .subscribe((res: UserReport | null) => {
        if (!res) return;

        res.totalCarbonUsed = +res.totalCarbonUsed.toFixed(2);
        res.totalCarbonSaved = +res.totalCarbonSaved.toFixed(2);
        res.totalSpent = +res.totalSpent.toFixed(2);

        this.report = res;

   
        setTimeout(() => this.renderCharts(), 100);
      });
  }

  renderCharts() {

    if (this.chartSaved) this.chartSaved.destroy();
    if (this.chartUsed) this.chartUsed.destroy();

    const savedCtx = document.getElementById('savedChart') as HTMLCanvasElement;
    const usedCtx = document.getElementById('usedChart') as HTMLCanvasElement;

    if (!savedCtx || !usedCtx || !this.report) return;


   const savedData = this.report.totalCarbonSaved > 0
  ? [12, 18, 25, 22, 35, 40, 30]
  : [0, 0, 0, 0, 0, 0, 0];


    this.chartSaved = new Chart(savedCtx, {
      type: 'line',
      data: {
        labels: ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'],
        datasets: [
          {
            label: "Carbon Saved (kg)",
            data: savedData,
            borderColor: "#22c55e",
            backgroundColor: "rgba(34,197,94,0.25)",
            tension: 0.4,
            fill: true,
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });

 
    const usedWeeklyData = [120, 160, 190, 170, 220, 260, 200]; 
 

    this.chartUsed = new Chart(usedCtx, {
      type: 'line',
      data: {
        labels: ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'],
        datasets: [
          {
            label: "Carbon Used (kg)",
            data: usedWeeklyData,
            borderColor: "#ef4444",
            backgroundColor: "rgba(239,68,68,0.25)",
            tension: 0.4,
            fill: true,
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }

  getBadgeColor(): string {
    if (!this.report || this.report.totalCarbonSaved <= 0)
      return 'from-gray-600 to-gray-500';

    const badge = this.report.ecoBadge;
    if (badge.includes('Eco Legend')) return 'from-yellow-500 to-amber-500';
    if (badge.includes('Green Hero')) return 'from-green-600 to-green-500';
    if (badge.includes('Conscious Shopper')) return 'from-blue-600 to-blue-500';
    if (badge.includes('Beginner')) return 'from-lime-600 to-lime-500';

    return 'from-gray-500 to-gray-400';
  }
}
