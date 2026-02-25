export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface JwtPayload {
  sub: string;      // email
  userId: number;
  username: string;
  role: string;
  exp: number;      // Unix timestamp in seconds
  iat: number;
}

export interface AuthUser {
  userId: number;
  email: string;
  username: string;
  role: string;
}
