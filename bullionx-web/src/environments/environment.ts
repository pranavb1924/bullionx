export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  appName: 'Bullion X',
  version: '1.0.0',
  debugMode: true,
  features: {
    enableWebSocket: false,
    enableNotifications: true,
    enableAnalytics: false
  },
  auth: {
    tokenKey: 'authToken',
    userKey: 'user',
    tokenExpiry: 3600000, // 1 hour in milliseconds
  },
  refreshInterval: 30000, // 30 seconds for data refresh
  maxRetries: 3,
  retryDelay: 1000
};