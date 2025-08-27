// src/app/core/models/auth.models.ts

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  firstName: string;
  lastName: string;
  userId: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  email: string;
  message: string;
}

// src/app/core/models/portfolio.models.ts

export interface HoldingView {
  symbol: string;
  quantity: number;
  averageCost: number;
  currentPrice: number;
  value: number;
  dayGainAmount: number;
  dayGainPercent: number;
  netPLAmount: number;
  netPLPercent: number;
}

export interface PortfolioTotals {
  totalValue: number;
  totalNetPLAmount: number;
  totalNetPLPercent: number;
}

export interface HoldingsResponse {
  holdings: HoldingView[];
  totals: PortfolioTotals;
  portfolioId: string; 
}

// src/app/core/models/market.models.ts

export interface StockDetails {
  symbol: string;
  currentPrice: number;
  change: number;
  percentChange: number;
  high: number;
  low: number;
  open: number;
  previousClose: number;
  timestamp: number;
}

// src/app/core/models/trade.models.ts

export interface BuyRequest {
  portfolioId: string;
  symbol: string;
  quantity: number;
}

export interface SellRequest {
  portfolioId: string;
  symbol: string;
  quantity: number;
}

export interface TradeResult {
  symbol: string;
  filledQty: number;
  priceUsd: number;
  filledAt: string;
}