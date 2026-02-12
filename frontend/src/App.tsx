import { Route, Routes } from "react-router-dom";
import { LandingPage } from "./pages/LandingPage/LandingPage";
import { SignUpPage } from "./pages/SignUpPage/SignUpPage";

export default function App () {
  return (
    <>
      <Routes>
        <Route path="/" element={<LandingPage/>}/>
        <Route path="/signup" element={<SignUpPage/>}/>
      </Routes>
    </>
  )
}