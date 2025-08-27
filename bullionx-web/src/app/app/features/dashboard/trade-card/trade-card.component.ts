import { Component, EventEmitter, Input, Output, signal, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MarketService } from '../../../core/market.service';
import { TradesService } from '../../../core/trades.service';
import { StockDetails } from '../../../core/auth.models';

@Component({
  selector: 'bx-trade-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './trade-card.component.html',
  styleUrls: ['./trade-card.component.css']
})
export class TradeCardComponent implements OnChanges {
  @Input() portfolioId = '';
  @Input() portfolioBalance = 0;
  @Input() selectedSymbol = '';
  @Output() traded = new EventEmitter<void>();

  symbol = signal<string>('');
  quantity = signal<number>(0);
  quote = signal<StockDetails | null>(null);
  loading = signal(false);
  message = signal<string | null>(null);
  error = signal<string | null>(null);

  constructor(
    private marketService: MarketService,
    private tradesService: TradesService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedSymbol'] && changes['selectedSymbol'].currentValue) {
      this.symbol.set(changes['selectedSymbol'].currentValue);
      this.lookup();
    }
  }

  lookup(): void {
    const sym = this.symbol().trim().toUpperCase();
    if (!sym) {
      this.error.set('Enter a symbol');
      return;
    }
    
    this.error.set(null);
    this.message.set(null);
    this.loading.set(true);
    
    this.marketService.getStock(sym).subscribe({
      next: (stockDetails) => {
        this.loading.set(false);
        this.quote.set(stockDetails);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Lookup failed');
      }
    });
  }

  buy(): void {
    this.executeTrade('buy');
  }

  sell(): void {
    this.executeTrade('sell');
  }

  private executeTrade(type: 'buy' | 'sell'): void {
    const sym = this.symbol().trim().toUpperCase();
    const qty = this.quantity();
    
    if (!this.portfolioId) {
      this.error.set('No portfolio ID');
      return;
    }
    if (!sym || qty <= 0) {
      this.error.set('Enter valid symbol and quantity');
      return;
    }

    

    
    this.error.set(null);
    this.message.set(null);
    this.loading.set(true);
    
    const request = {
      portfolioId: this.portfolioId,
      balance: this.portfolioBalance,
      symbol: sym,
      quantity: qty
    };
    
    const operation = type === 'buy' 
      ? this.tradesService.buy(request)
      : this.tradesService.sell(request);
    
    operation.subscribe({
      next: () => {
        this.loading.set(false);
        this.message.set(`${type === 'buy' ? 'Bought' : 'Sold'} ${qty} shares of ${sym}`);
        this.quantity.set(0);
        this.traded.emit();
      },
      error: (err: { error: { message: any; }; }) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || `${type} failed`);
      }
    });
  }
}