import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HoldingsResponse } from '../../../core/auth.models';

@Component({
  selector: 'bx-portfolio-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './portfolio-summary.component.html',
  styleUrls: ['./portfolio-summary.component.css']
})
export class PortfolioSummaryComponent {
  @Input() holdings: HoldingsResponse | null = null;
  @Input() cashBalance: number = 0;
  
  get totalAccountValue(): number {
    if (!this.holdings?.totals?.totalValue) return this.cashBalance;
    return Number(this.holdings.totals.totalValue) + this.cashBalance;
  }
  
  get positionsCount(): number {
    return this.holdings?.holdings?.length || 0;
  }
  
  get totalPL(): number {
    return Number(this.holdings?.totals?.totalNetPLAmount) || 0;
  }
  
  get totalPLPercent(): number {
    return Number(this.holdings?.totals?.totalNetPLPercent) || 0;
  }
}