import { Route, Routes } from "react-router-dom";
import { LandingPage } from "./pages/LandingPage/LandingPage";
import { SignUpPage } from "./pages/SignUpPage/SignUpPage";
import { HomePage } from "./pages/HomePage/HomePage";

export default function App () {
  return (
    <>
      <Routes>
        <Route path="/" element={<LandingPage/>}/>
        <Route path="/signup" element={<SignUpPage/>}/>

        <Route path="/home" element={<HomePage/>} />
      </Routes>
    </>
  )
}