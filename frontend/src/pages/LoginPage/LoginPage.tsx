import { ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";
import { SignUpMapVisual } from "@/pages/SignUpPage/SignUpMapVisual";
import { SignUpFooter } from "@/pages/SignUpPage/SignUpFooter";
import AdventuraIcon from "@/components/common/AdventuraIcon";
import { LoginForm } from "./LoginForm";

export function LoginPage() {
  return (
    <div className="flex min-h-screen">
      {/* Left: form */}
      <div className="flex w-full flex-col px-8 py-6 lg:w-1/2 lg:px-16 xl:px-24">
        {/* Topbar */}
        <div className="flex items-center justify-between">
          <Link
            to="/"
            className="flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 transition-colors hover:bg-gray-50"
          >
            <ArrowLeft className="h-5 w-5 text-gray-600" />
          </Link>
          <p className="text-sm text-gray-500">
            Não tem conta?{" "}
            <Link
              to="/signup"
              className="font-semibold text-gray-900 underline underline-offset-4"
            >
              Registrar
            </Link>
          </p>
        </div>

        {/* Form area */}
        <div className="mx-auto mt-10 flex w-full max-w-md flex-1 flex-col">
          {/* Logo */}
          <div className="flex flex-col items-center gap-3">
            <div className="flex h-16 w-16 items-center justify-center rounded-full border border-gray-200 bg-white shadow-sm">
              <AdventuraIcon size={38} className="text-[#dda600]" />
            </div>
            <div className="text-center">
              <h1 className="text-3xl font-bold tracking-tight text-gray-900">
                Bem-vindo de volta
              </h1>
              <p className="mt-1.5 text-sm text-gray-400">
                Insira as suas credenciais para continuar
              </p>
            </div>
          </div>

          <LoginForm />
        </div>

        <div className="flex flex-auto flex-col items-center">
          <SignUpFooter />
        </div>
      </div>

      {/* Right: map visual */}
      <SignUpMapVisual />
    </div>
  );
}
