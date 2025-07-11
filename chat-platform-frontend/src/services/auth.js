// Service d'authentification amélioré
export const authService = {
  // Stocker le token
  setToken: (token) => {
    localStorage.setItem("authToken", token)
  },

  // Récupérer le token
  getToken: () => {
    return localStorage.getItem("authToken")
  },

  // Supprimer le token
  removeToken: () => {
    localStorage.removeItem("authToken")
  },

  // Vérifier si l'utilisateur est connecté
  isAuthenticated: () => {
    const token = localStorage.getItem("authToken")
    if (!token) return false

    try {
      // Vérifier si le token n'est pas expiré
      const payload = JSON.parse(atob(token.split(".")[1]))
      const currentTime = Date.now() / 1000
      return payload.exp > currentTime
    } catch (error) {
      console.error("Erreur lors de la vérification du token:", error)
      return false
    }
  },

  // Obtenir les informations de l'utilisateur depuis le token
  getUserInfo: () => {
    const token = localStorage.getItem("authToken")
    if (!token) return null

    try {
      const payload = JSON.parse(atob(token.split(".")[1]))
      return {
        id: payload.userId,
        username: payload.sub,
        email: payload.email,
      }
    } catch (error) {
      console.error("Erreur lors de l'extraction des infos utilisateur:", error)
      return null
    }
  },

  // Déconnexion
  logout: () => {
    localStorage.removeItem("authToken")
    // Ne pas rediriger automatiquement, laisser le composant gérer
  },

  // Vérifier si le token va expirer bientôt (dans les 5 prochaines minutes)
  isTokenExpiringSoon: () => {
    const token = localStorage.getItem("authToken")
    if (!token) return false

    try {
      const payload = JSON.parse(atob(token.split(".")[1]))
      const currentTime = Date.now() / 1000
      const fiveMinutesFromNow = currentTime + 5 * 60
      return payload.exp < fiveMinutesFromNow
    } catch (error) {
      return false
    }
  },

  // Obtenir le header d'autorisation
  getAuthHeader: () => {
    const token = localStorage.getItem("authToken")
    return token ? `Bearer ${token}` : null
  },
}
