"use client"

import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { apiService } from "../api/api"
import { authService } from "../services/auth"

const Register = () => {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
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

    // Validation username
    if (!formData.username.trim()) {
      newErrors.username = "Le nom d'utilisateur est obligatoire"
    } else if (formData.username.length < 3) {
      newErrors.username = "Le nom d'utilisateur doit contenir au moins 3 caractères"
    }

    // Validation email
    if (!formData.email.trim()) {
      newErrors.email = "L'email est obligatoire"
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Format d'email invalide"
    }

    // Validation password
    if (!formData.password) {
      newErrors.password = "Le mot de passe est obligatoire"
    } else if (formData.password.length < 8) {
      newErrors.password = "Le mot de passe doit contenir au moins 8 caractères"
    }

    // Validation confirm password
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Veuillez confirmer le mot de passe"
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Les mots de passe ne correspondent pas"
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
      const response = await apiService.register({
        username: formData.username,
        email: formData.email,
        password: formData.password,
      })

      // Stocker le token
      authService.setToken(response.data.token)

      setMessage("Inscription réussie ! Redirection...")

      // Rediriger vers la page d'accueil après 1 seconde
      setTimeout(() => {
        navigate("/dashboard")
      }, 1000)
    } catch (error) {
      console.error("Erreur d'inscription:", error)

      if (error.response?.data) {
        if (typeof error.response.data === "object" && error.response.data.error) {
          setMessage(error.response.data.error)
        } else if (typeof error.response.data === "object") {
          // Erreurs de validation du backend
          setErrors(error.response.data)
        } else {
          setMessage("Erreur lors de l'inscription")
        }
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
            <h2 className="text-3xl font-bold text-gray-900">Inscription</h2>
            <p className="text-gray-600 mt-2">Créez votre compte pour commencer</p>
          </div>

          {/* Message de succès */}
          {message && !errors.username && !errors.email && (
            <div className="alert-success mb-4">
              <p>{message}</p>
            </div>
          )}

          {/* Message d'erreur général */}
          {message && (errors.username || errors.email) && (
            <div className="alert-error mb-4">
              <p>{message}</p>
            </div>
          )}

          {/* Formulaire */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Username */}
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-1">
                Nom d'utilisateur
              </label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleChange}
                className={`input-field ${errors.username ? "border-red-500" : ""}`}
                placeholder="Votre nom d'utilisateur"
              />
              {errors.username && <p className="text-red-600 text-sm mt-1">{errors.username}</p>}
            </div>

            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                className={`input-field ${errors.email ? "border-red-500" : ""}`}
                placeholder="votre@email.com"
              />
              {errors.email && <p className="text-red-600 text-sm mt-1">{errors.email}</p>}
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
                placeholder="Au moins 8 caractères"
              />
              {errors.password && <p className="text-red-600 text-sm mt-1">{errors.password}</p>}
            </div>

            {/* Confirm Password */}
            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">
                Confirmer le mot de passe
              </label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                className={`input-field ${errors.confirmPassword ? "border-red-500" : ""}`}
                placeholder="Répétez votre mot de passe"
              />
              {errors.confirmPassword && <p className="text-red-600 text-sm mt-1">{errors.confirmPassword}</p>}
            </div>

            {/* Submit Button */}
            <button type="submit" className="btn-primary w-full" disabled={loading}>
              {loading ? (
                <div className="flex items-center justify-center">
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                  Inscription en cours...
                </div>
              ) : (
                "S'inscrire"
              )}
            </button>
          </form>

          {/* Link to Login */}
          <div className="text-center mt-6">
            <p className="text-gray-600">
              Déjà un compte ?{" "}
              <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
                Se connecter
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Register
