"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { authService } from "../services/auth"
import { apiService } from "../api/api"
import Navbar from "./Navbar"

const Dashboard = () => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const navigate = useNavigate()

  useEffect(() => {
    loadUserInfo()
  }, [])

  const loadUserInfo = async () => {
    try {
      setLoading(true)
      const userInfo = authService.getUserInfo()
      if (userInfo) {
        setUser(userInfo)
      } else {
        // Essayer de récupérer les infos depuis l'API
        const response = await apiService.getCurrentUser()
        setUser(response.data)
      }
    } catch (err) {
      console.error("Erreur lors du chargement des informations utilisateur:", err)
      setError("Impossible de charger les informations utilisateur")
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    authService.logout()
    navigate("/login")
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="flex items-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mr-3"></div>
          <span className="text-gray-600">Chargement...</span>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-br">
      {/* Header */}
      <Navbar />

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {error && (
          <div className="alert-error mb-6">
            <p>{error}</p>
          </div>
        )}

        {/* Welcome Card */}
        <div className="card mb-8">
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">Bienvenue {user?.username} ! 🎉</h2>
          <p className="text-gray-600 mb-4">
            Vous êtes maintenant connecté à la plateforme de discussion. Voici ce que vous pouvez faire :
          </p>
          <div className="grid grid-cols-3 gap-6">
            <div className="text-center p-4 bg-blue-50 rounded-lg">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-3">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
              </div>
              <h3 className="font-medium text-gray-800">Contacts</h3>
              <p className="text-sm text-gray-600 mt-1">Gérez vos contacts</p>
              <button className="btn-primary mt-3 text-sm">Bientôt disponible</button>
            </div>

            <div className="text-center p-4 bg-green-50 rounded-lg">
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mx-auto mb-3">
                <svg className="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                  />
                </svg>
              </div>
              <h3 className="font-medium text-gray-800">Messages</h3>
              <p className="text-sm text-gray-600 mt-1">Envoyez des messages</p>
              <button className="btn-primary mt-3 text-sm">Bientôt disponible</button>
            </div>

            <div className="text-center p-4 bg-purple-50 rounded-lg">
              <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mx-auto mb-3">
                <svg className="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
              </div>
              <h3 className="font-medium text-gray-800">Profil</h3>
              <p className="text-sm text-gray-600 mt-1">Gérez votre profil</p>
              <button className="btn-primary mt-3 text-sm">Bientôt disponible</button>
            </div>
          </div>
        </div>

        {/* User Info Card */}
        {user && (
          <div className="card">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Informations du compte</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Nom d'utilisateur:</span>
                <span className="font-medium text-gray-900">{user.username}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Email:</span>
                <span className="font-medium text-gray-900">{user.email}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">ID utilisateur:</span>
                <span className="font-medium text-gray-900">{user.id}</span>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default Dashboard
