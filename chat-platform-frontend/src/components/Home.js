"use client"

import { useState, useEffect } from "react"
import { apiService } from "../api/api"
import { Link } from "react-router-dom"

const Home = () => {
  const [backendStatus, setBackendStatus] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    testBackendConnection()
  }, [])

  const testBackendConnection = async () => {
    try {
      setLoading(true)
      const response = await apiService.test()
      setBackendStatus(response.data)
      setError(null)
    } catch (err) {
      setError("Impossible de se connecter au backend")
      console.error("Erreur de connexion:", err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br">
      <div className="container mx-auto px-4 py-16">
        <div className="max-w-4xl mx-auto text-center">
          {/* Header */}
          <div className="mb-12">
            <h1 className="text-5xl font-bold text-gray-900 mb-4">Plateforme de Discussion</h1>
            <p className="text-xl text-gray-600 mb-8">Une application de messagerie moderne inspirée de WhatsApp</p>
            <div className="w-24 h-1 bg-blue-600 mx-auto rounded-lg"></div>
          </div>

          {/* Status Card */}
          <div className="card max-w-md mx-auto mb-12">
            <h2 className="text-2xl font-semibold text-gray-800 mb-4">État du Système</h2>

            {loading && (
              <div className="flex items-center justify-center py-4">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                <span className="ml-2 text-gray-600">Test de connexion...</span>
              </div>
            )}

            {error && (
              <div className="alert-error mb-4">
                <div className="flex items-center">
                  <div className="w-3 h-3 bg-red-500 rounded-full mr-2"></div>
                  <span className="text-red-700 font-medium">Backend: Déconnecté</span>
                </div>
                <p className="text-red-600 text-sm mt-1">{error}</p>
              </div>
            )}

            {backendStatus && (
              <div className="alert-success mb-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div className="w-3 h-3 bg-green-500 rounded-full mr-2"></div>
                    <span className="text-green-700 font-medium">Backend: Connecté</span>
                  </div>
                  <span className="text-green-600 text-sm">{backendStatus.status}</span>
                </div>
                <p className="text-green-600 text-sm mt-1">{backendStatus.message}</p>
                <p className="text-gray-500 text-xs mt-1">
                  Dernière vérification: {new Date(backendStatus.timestamp).toLocaleString()}
                </p>
              </div>
            )}

            <button onClick={testBackendConnection} className="btn-primary w-full" disabled={loading}>
              {loading ? "Test en cours..." : "Tester la connexion"}
            </button>
          </div>

          {/* Features Preview */}
          <div className="grid grid-cols-3 gap-8 mb-12">
            <div className="card text-center">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                  />
                </svg>
              </div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">Messages</h3>
              <p className="text-gray-600 text-sm">Envoyez des messages texte et des fichiers</p>
            </div>

            <div className="card text-center">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
              </div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">Groupes</h3>
              <p className="text-gray-600 text-sm">Créez et gérez des groupes de discussion</p>
            </div>

            <div className="card text-center">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mx-auto mb-4">
                <svg className="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                  />
                </svg>
              </div>
              <h3 className="text-lg font-semibold text-gray-800 mb-2">Profil</h3>
              <p className="text-gray-600 text-sm">Gérez votre profil et vos paramètres</p>
            </div>
          </div>

          {/* Next Steps */}
          <div className="card">
            <h2 className="text-2xl font-semibold text-gray-800 mb-4">Commencer</h2>

            {/* Boutons d'action */}
            <div className="flex flex-col sm:flex-row gap-4 mb-6">
              <Link to="/register" className="btn-primary text-center flex-1">
                Créer un compte
              </Link>
              <Link to="/login" className="btn-secondary text-center flex-1">
                Se connecter
              </Link>
            </div>

            <div className="text-left space-y-2">
              <div className="flex items-center">
                <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                <span className="text-gray-700">✅ Configuration initiale terminée</span>
              </div>
              <div className="flex items-center">
                <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                <span className="text-gray-700">✅ Authentification (Inscription/Connexion)</span>
              </div>
              <div className="flex items-center">
                <div className="w-2 h-2 bg-yellow-500 rounded-full mr-3"></div>
                <span className="text-gray-700">🔄 Prochaine étape: Gestion des contacts</span>
              </div>
              <div className="flex items-center">
                <div className="w-2 h-2 bg-gray-300 rounded-full mr-3"></div>
                <span className="text-gray-500">⏳ À venir: Groupes de discussion</span>
              </div>
              <div className="flex items-center">
                <div className="w-2 h-2 bg-gray-300 rounded-full mr-3"></div>
                <span className="text-gray-500">⏳ À venir: Messages et fichiers</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home
