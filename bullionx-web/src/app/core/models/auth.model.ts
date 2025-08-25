export interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  user: {
    email: string;
    firstName: string;
    lastName: string;
  } | null;
}