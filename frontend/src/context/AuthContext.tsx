import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode } from "react";
import { jwtDecode } from "jwt-decode";
import { api } from "@/services/api";
import type { AuthUser, JwtPayload, LoginRequest, RegisterRequest } from "@/types/user";

function tokenToAuthUser(token: string): AuthUser | null {
  try {
    const payload = jwtDecode<JwtPayload>(token);
    if (Date.now() >= payload.exp * 1000) return null; // expired
    return {
      userId: payload.userId,
      email: payload.sub,
      username: payload.username,
      role: payload.role,
    };
  } catch {
    return null;
  }
}

interface AuthContextValue {
  user: AuthUser | null;
  token: string | null;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem("jwt");
    if (stored) {
      const parsed = tokenToAuthUser(stored);
      if (parsed) {
        setToken(stored);
        setUser(parsed);
      } else {
        localStorage.removeItem("jwt"); // expired token
      }
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginRequest): Promise<void> => {
    const response = await api.post("/authenticate", credentials);
    const rawHeader = response.headers["authorization"] as string | undefined;
    if (!rawHeader) throw new Error("No Authorization header in response");
    const jwt = rawHeader.startsWith("Bearer ") ? rawHeader.slice(7) : rawHeader;
    const parsed = tokenToAuthUser(jwt);
    if (!parsed) throw new Error("Invalid JWT received");
    localStorage.setItem("jwt", jwt);
    setToken(jwt);
    setUser(parsed);
  };

  const register = async (data: RegisterRequest): Promise<void> => {
    await api.post("/user/register", data);
  };

  const logout = (): void => {
    localStorage.removeItem("jwt");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
  return ctx;
}
