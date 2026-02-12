import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ArrowRight, CheckCircle2, Eye, EyeOff, Lock, Mail, User } from "lucide-react";

import { useState } from "react";

export function SignUpForm () {

      const [name, setName] = useState("");
      const [email, setEmail] = useState("");
      const [password, setPassword] = useState("");
      const [confirmPassword, setConfirmPassword] = useState("");
      const [showPassword, setShowPassword] = useState(false);
      const [showConfirmPassword, setShowConfirmPassword] = useState(false);

      const hasMinLength = password.length >= 8;
      const hasNumberOrSymbol = /[0-9!@#$%^&*(),.?":{}|<>]/.test(password);
      const hasMixedCase = /[a-z]/.test(password) && /[A-Z]/.test(password);

      const isNameValid = name.trim().length >= 2;
      const isEmailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
      const isPasswordValid = hasMinLength && hasNumberOrSymbol && hasMixedCase;
      const isConfirmValid = confirmPassword.length > 0 && confirmPassword === password;

      const isFormValid = isNameValid && isEmailValid && isPasswordValid && isConfirmValid;

      const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!isFormValid) return;
        // TODO: call signup API
      };

    return (
        <form onSubmit={handleSubmit} className="mt-10 flex flex-col gap-6">
            {/* Name */}
            <div className="relative">
              <User className="absolute top-1/2 left-3 h-5 w-5 -translate-y-1/2 text-gray-400" />
              <Label>
                <Input
                autoComplete="name"
                placeholder="Nome Completo"
                value={name}
                onChange={(e) => setName(e.target.value)}
                id="name"
                className="h-12 rounded-lg border-gray-200 pl-11 pr-10 text-base"
                />
              </Label>
              {isNameValid && (
                <CheckCircle2 className="absolute top-1/2 right-3 h-5 w-5 -translate-y-1/2 text-emerald-500" />
              )}
            </div>

            {/* Email */}
            <div className="relative">
              <Mail className="absolute top-1/2 left-3 h-5 w-5 -translate-y-1/2 text-gray-400" />
              <Label>
                <Input
                autoComplete="email"
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="h-12 rounded-lg border-gray-200 pl-11 pr-10 text-base"
                />
              </Label>

              {isEmailValid && (
                <CheckCircle2 className="absolute top-1/2 right-3 h-5 w-5 -translate-y-1/2 text-emerald-500" />
              )}
            </div>

            {/* Password */}
            <div>
              <div className="relative">
                <Lock className="absolute top-1/2 left-3 h-5 w-5 -translate-y-1/2 text-gray-400" />
                <Label>
                  <Input
                  autoComplete="new password"
                  type={showPassword ? "text" : "password"}
                  placeholder="Senha"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="h-12 rounded-lg border-gray-200 pl-11 pr-10 text-base"
                  />
                </Label>
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute top-1/2 right-3 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? (
                    <Eye className="h-5 w-5" />
                  ) : (
                    <EyeOff className="h-5 w-5" />
                  )}
                </button>
              </div>

              {/* Password rules */}
              {password.length > 0 && (
                <ul className="mt-3 space-y-1 text-sm">
                  <li className={hasMinLength ? "text-emerald-500" : "text-gray-400"}>
                    <span className="mr-1.5 inline-block">{hasMinLength ? "\u2713" : "\u2022"}</span>
                    Pelo menos 8 caracteres
                  </li>
                  <li className={hasNumberOrSymbol ? "text-emerald-500" : "text-gray-400"}>
                    <span className="mr-1.5 inline-block">{hasNumberOrSymbol ? "\u2713" : "\u2022"}</span>
                    Pelo menos um número (0-9) ou símbolo
                  </li>
                  <li className={hasMixedCase ? "text-emerald-500" : "text-gray-400"}>
                    <span className="mr-1.5 inline-block">{hasMixedCase ? "\u2713" : "\u2022"}</span>
                    Minúsculas (a-z) e maiúsculas (A-Z)
                  </li>
                </ul>
              )}
            </div>

            {/* Confirm Password */}
            <div>
                <div className="relative">
                <Lock className="absolute top-1/2 left-3 h-5 w-5 -translate-y-1/2 text-gray-400" />
                <Label>
                  <Input
                autoComplete="new-password"
                type={showConfirmPassword ? "text" : "password"}
                placeholder="Confirmar Senha"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                id="confirm-password"
                className="h-12 rounded-lg border-gray-200 pl-11 pr-10 text-base"
                />
              </Label>

              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute top-1/2 right-3 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                {showConfirmPassword ? (
                  <Eye className="h-5 w-5" />
                ) : (
                  <EyeOff className="h-5 w-5" />
                )}
              </button>

              {isConfirmValid && (
                <CheckCircle2 className="absolute top-1/2 right-10 h-5 w-5 -translate-y-1/2 text-emerald-500" />
              )}
              </div>
              {confirmPassword.length > 0 && !isConfirmValid && (
                <p className="mt-2 text-sm text-red-500">As palavras-passe não coincidem</p>
              )}
              <div className="flex items-center space-x-2 mt-5">
                    <Checkbox 
                      id="terms" 
                      className="rounded-full cursor-pointer"
                    />
                    <Label htmlFor="terms" className="text-xs text-muted-foreground">
                      Eu li e concordo com os{" "}
                      <a href="/terms" target="_blank" rel="noopener noreferrer" className="text-gradient-primary hover:underline">
                        Termos de Serviço
                      </a>
                    </Label>
                  </div>
            </div>

            {/* Submit */}
            <Button
              type="submit"
              disabled={!isFormValid}
              className="h-13 w-full rounded-full bg-gradient-primary text-lg font-semibold text-white hover:bg/50"
            >
              Registrar
              <ArrowRight className="ml-0 h-5 w-5" />
            </Button>
          </form>
    )
}
