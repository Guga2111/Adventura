import { SignUpTopbar } from "./SignUpTopbar";
import { SignUpForm } from "./SignUpForm";
import { SignUpMapVisual } from "./SignUpMapVisual";
import { SignUpFooter } from "./SignUpFooter";

export function SignUpPage() {

  return (
    <div className="flex min-h-screen">

      <div className="flex w-full flex-col px-8 py-6 lg:w-1/2 lg:px-16 xl:px-24">
        <SignUpTopbar />
        
        <div className="mx-auto mt-10 flex w-full max-w-md flex-1 flex-col">
            <h1 className="text-4xl font-bold tracking-tight text-gray-900">Registrar</h1>
            <div>
              <p className="mt-2 text-sm text-gray-400">Planeja a sua Próxima Aventura com a Adventura</p>
            </div>
            <SignUpForm />
        </div>
        <div className="flex flex-auto flex-col items-center">
          <SignUpFooter />
        </div>
      </div>
        <SignUpMapVisual />
    </div>
  );
}
