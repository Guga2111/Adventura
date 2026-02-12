import { ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";

export function SignUpTopbar () {
    return (
        <div className="flex items-center justify-between">
          <Link
            to="/"
            className="flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 transition-colors hover:bg-gray-50"
          >
            <ArrowLeft className="h-5 w-5 text-gray-600" />
          </Link>
          <p className="text-sm text-gray-500">
            Já tem conta?{" "}
            <Link to="/login" className="font-semibold text-gray-900 underline underline-offset-4">
              Entrar
            </Link>
          </p>
        </div>
    )
}
