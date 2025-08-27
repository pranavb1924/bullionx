import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BuyRequest, SellRequest, TradeResult, HoldingsResponse } from './auth.models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TradesService {
  constructor(private http: HttpClient) {}

  buy(request: BuyRequest): Observable<TradeResult> {
    return this.http.post<TradeResult>(
      `${environment.apiUrl}/api/trades/buy`,
      request
    );
  }

  sell(request: SellRequest): Observable<TradeResult> {
    return this.http.post<TradeResult>(
      `${environment.apiUrl}/api/trades/sell`,
      request
    );
  }
}