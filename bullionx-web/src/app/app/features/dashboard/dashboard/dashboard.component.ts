import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth.service';
import { PortfolioService } from '../../../core/portfolio.service';
import { TradeCardComponent } from '../trade-card/trade-card.component';
import { GreetingComponent } from '../greeting/greeting.component';
import { HoldingsComponent } from '../holdings/holdings.component';
import { PortfolioSummaryComponent } from '../portfolio-summary/portfolio-summary.component';
import { HoldingsResponse } from '../../../core/auth.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    TradeCardComponent,
    GreetingComponent,
    HoldingsComponent,
    PortfolioSummaryComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  portfolioId = signal<string>('');
  holdings = signal<HoldingsResponse | null>(null);
  cashBalance = signal<number>(10000);
  selectedSymbol = signal<string>('');
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(
    private authService: AuthService,
    private portfolioService: PortfolioService
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadPortfolioAndHoldings();
  }

  private loadUserData(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.portfolioId.set(userId);
    }
  }

  private loadPortfolioAndHoldings(): void {
    this.loading.set(true);
    this.error.set(null);
    
    const userId = this.authService.getUserId();
    if (!userId) {
      this.error.set('No user ID found');
      this.loading.set(false);
      return;
    }

    this.portfolioService.getCurrentUserHoldings()
      .subscribe({
        next: (response) => {
          this.holdings.set(response);
          this.portfolioId.set(response.portfolioId);
          // If your backend returns cashBalance in the response, use it
          if ('cashBalance' in response && response.cashBalance !== undefined) {
            this.cashBalance.set(Number(response.cashBalance));
          }
          this.loading.set(false);
        },
        error: (err) => {
          this.error.set(err?.error?.message || 'Failed to load portfolio');
          this.loading.set(false);
        }
      });
  }

  onTraded(): void {
    this.loadPortfolioAndHoldings();
  }

  onHoldingSelected(symbol: string): void {
    this.selectedSymbol.set(symbol);
  }
}