import { Navigate } from "react-router-dom"
import { authService } from "../services/auth"

const PrivateRoute = ({ children }) => {
  const isAuthenticated = authService.isAuthenticated()

  if (!isAuthenticated) {
    // Rediriger vers la page de connexion si non authentifié
    return <Navigate to="/login" replace />
  }

  return children
}

export default PrivateRoute
