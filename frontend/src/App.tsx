import { Route, Routes } from "react-router-dom";
import { AuthProvider } from "@/context/AuthContext";
import { ProtectedRoute } from "@/router/ProtectedRoute";
import { LandingPage } from "./pages/LandingPage/LandingPage";
import { SignUpPage } from "./pages/SignUpPage/SignUpPage";
import { LoginPage } from "./pages/LoginPage/LoginPage";
import { HomePage } from "./pages/HomePage/HomePage";

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/login" element={<LoginPage />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/home" element={<HomePage />} />
        </Route>
      </Routes>
    </AuthProvider>
  );
}
