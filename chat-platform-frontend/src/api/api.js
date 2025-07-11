import axios from "axios"

// Configuration de base d'Axios
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
})

// Intercepteur pour ajouter le token JWT aux requêtes
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("authToken")
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Intercepteur pour gérer les réponses et erreurs
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem("authToken")
      window.location.href = "/login"
    }
    return Promise.reject(error)
  },
)

// Fonctions API
export const apiService = {
  // Test de connexion
  test: () => api.get("/test"),

  // Authentification
  register: (userData) => api.post("/auth/register", userData),
  login: (credentials) => api.post("/auth/login", credentials),
  logout: () => api.post("/auth/logout"),
  verifyToken: () => api.get("/auth/verify"),

  // Utilisateur actuel
  getCurrentUser: () => api.get("/test/me"),

  // Contacts (à implémenter)
  getContacts: () => api.get("/contacts"),
  addContact: (contactData) => api.post("/contacts", contactData),
  deleteContact: (contactId) => api.delete(`/contacts/${contactId}`),

  // Messages (à implémenter)
  getMessages: (contactId) => api.get(`/messages?contactId=${contactId}`),
  sendMessage: (messageData) => api.post("/messages", messageData),

  // Groupes (à implémenter)
  getGroups: () => api.get("/groups"),
  createGroup: (groupData) => api.post("/groups", groupData),
}

export default api
