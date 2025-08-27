import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HoldingsResponse } from './auth.models';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getHoldings(portfolioId: string): Observable<HoldingsResponse> {
    return this.http.get<HoldingsResponse>(
      `${environment.apiUrl}/api/portfolio/${portfolioId}/holdings`
    );
  }

// portfolio.service.ts
getCurrentUserHoldings(): Observable<HoldingsResponse> {
  const userId = this.authService.getUserId();
  if (!userId) {
    throw new Error('No user ID available');
  }
  return this.http.get<HoldingsResponse>(
    `${environment.apiUrl}/api/portfolio/user/${userId}/holdings`
  );
}
}