import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"
import Home from "./components/Home"
import Login from "./components/Login"
import Register from "./components/Register"
import Dashboard from "./components/Dashboard"
import PrivateRoute from "./components/PrivateRoute"
import { authService } from "./services/auth"
import "./index.css"

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Route publique - Page d'accueil */}
          <Route path="/" element={authService.isAuthenticated() ? <Navigate to="/dashboard" replace /> : <Home />} />

          {/* Routes d'authentification */}
          <Route
            path="/login"
            element={authService.isAuthenticated() ? <Navigate to="/dashboard" replace /> : <Login />}
          />
          <Route
            path="/register"
            element={authService.isAuthenticated() ? <Navigate to="/dashboard" replace /> : <Register />}
          />

          {/* Routes protégées */}
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            }
          />

          {/* Route par défaut - redirection */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
