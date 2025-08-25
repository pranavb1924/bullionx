import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Portfolio {
  totalValue: number;
  totalGainLoss: number;
  totalGainLossPercent: number;
  holdings: Holding[];
}

export interface Holding {
  symbol: string;
  name: string;
  quantity: number;
  price: number;
  value: number;
  change: number;
  changePercent: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private portfolioSubject = new BehaviorSubject<Portfolio | null>(null);
  public portfolio$ = this.portfolioSubject.asObservable();

  constructor(private api: ApiService) {}

  init(): void {
    // Load initial dashboard data
    this.loadPortfolio();
  }

  destroy(): void {
    // Clean up subscriptions if needed
  }

  loadPortfolio(): void {
    // Mock data for now - replace with actual API call
    const mockPortfolio: Portfolio = {
      totalValue: 125430.50,
      totalGainLoss: 5230.25,
      totalGainLossPercent: 4.35,
      holdings: [
        {
          symbol: 'AAPL',
          name: 'Apple Inc.',
          quantity: 100,
          price: 178.50,
          value: 17850,
          change: 2.35,
          changePercent: 1.33
        },
        {
          symbol: 'GOOGL',
          name: 'Alphabet Inc.',
          quantity: 50,
          price: 142.30,
          value: 7115,
          change: -1.20,
          changePercent: -0.84
        },
        {
          symbol: 'MSFT',
          name: 'Microsoft Corp.',
          quantity: 75,
          price: 378.20,
          value: 28365,
          change: 5.45,
          changePercent: 1.46
        }
      ]
    };
    
    this.portfolioSubject.next(mockPortfolio);
  }

  refreshPortfolio(): void {
    this.loadPortfolio();
  }
}