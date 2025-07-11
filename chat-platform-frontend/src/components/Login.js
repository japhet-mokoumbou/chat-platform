"use client"

import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { apiService } from "../api/api"
import { authService } from "../services/auth"

const Login = () => {
  const [formData, setFormData] = useState({
    usernameOrEmail: "",
    password: "",
  })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState("")

  const navigate = useNavigate()

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
    // Effacer l'erreur du champ modifié
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }))
    }
  }

  const validateForm = () => {
    const newErrors = {}

    if (!formData.usernameOrEmail.trim()) {
      newErrors.usernameOrEmail = "Le nom d'utilisateur ou email est obligatoire"
    }

    if (!formData.password) {
      newErrors.password = "Le mot de passe est obligatoire"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!validateForm()) {
      return
    }

    setLoading(true)
    setMessage("")
    setErrors({})

    try {
      const response = await apiService.login(formData)

      // Stocker le token
      authService.setToken(response.data.token)

      setMessage("Connexion réussie ! Redirection...")

      // Rediriger vers le dashboard après 1 seconde
      setTimeout(() => {
        navigate("/dashboard")
      }, 1000)
    } catch (error) {
      console.error("Erreur de connexion:", error)

      if (error.response?.data?.error) {
        setMessage(error.response.data.error)
      } else if (error.response?.data) {
        setErrors(error.response.data)
      } else {
        setMessage("Erreur de connexion au serveur")
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br py-12 px-4">
      <div className="max-w-md w-full">
        <div className="card">
          {/* Header */}
          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold text-gray-900">Connexion</h2>
            <p className="text-gray-600 mt-2">Connectez-vous à votre compte</p>
          </div>

          {/* Message de succès */}
          {message && !errors.usernameOrEmail && !errors.password && (
            <div className="alert-success mb-4">
              <p>{message}</p>
            </div>
          )}

          {/* Message d'erreur */}
          {message && (errors.usernameOrEmail || errors.password) && (
            <div className="alert-error mb-4">
              <p>{message}</p>
            </div>
          )}

          {/* Formulaire */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Username or Email */}
            <div>
              <label htmlFor="usernameOrEmail" className="block text-sm font-medium text-gray-700 mb-1">
                Nom d'utilisateur ou Email
              </label>
              <input
                type="text"
                id="usernameOrEmail"
                name="usernameOrEmail"
                value={formData.usernameOrEmail}
                onChange={handleChange}
                className={`input-field ${errors.usernameOrEmail ? "border-red-500" : ""}`}
                placeholder="Votre nom d'utilisateur ou email"
              />
              {errors.usernameOrEmail && <p className="text-red-600 text-sm mt-1">{errors.usernameOrEmail}</p>}
            </div>

            {/* Password */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                Mot de passe
              </label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                className={`input-field ${errors.password ? "border-red-500" : ""}`}
                placeholder="Votre mot de passe"
              />
              {errors.password && <p className="text-red-600 text-sm mt-1">{errors.password}</p>}
            </div>

            {/* Submit Button */}
            <button type="submit" className="btn-primary w-full" disabled={loading}>
              {loading ? (
                <div className="flex items-center justify-center">
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                  Connexion en cours...
                </div>
              ) : (
                "Se connecter"
              )}
            </button>
          </form>

          {/* Link to Register */}
          <div className="text-center mt-6">
            <p className="text-gray-600">
              Pas encore de compte ?{" "}
              <Link to="/register" className="text-blue-600 hover:text-blue-700 font-medium">
                S'inscrire
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Login
