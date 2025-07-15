import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FiUser, FiMail, FiLock } from "react-icons/fi";
import api from "../api/api";

export default function Register() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      // Appel API (à adapter selon ton backend)
      await api.post("/api/auth/register", { username, email, password });
      navigate("/login");
    } catch (err) {
      setError(err.response?.data?.error || "Erreur d'inscription");
    }
    setLoading(false);
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-100 to-blue-300 dark:from-gray-900 dark:to-gray-800">
      <form onSubmit={handleSubmit} className="bg-white dark:bg-gray-950 shadow-xl rounded-2xl p-8 w-full max-w-md flex flex-col gap-6">
        <h2 className="text-3xl font-bold text-blue-600 text-center mb-2">Créer un compte ChatESP</h2>
        {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded text-center">{error}</div>}
        <div className="flex flex-col gap-2">
          <label className="text-gray-700 dark:text-gray-200 font-medium">Nom d'utilisateur</label>
          <div className="relative">
            <FiUser className="absolute left-3 top-3 text-gray-400" />
            <input
              type="text"
              className="w-full py-2 pl-10 pr-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={50}
            />
          </div>
        </div>
        <div className="flex flex-col gap-2">
          <label className="text-gray-700 dark:text-gray-200 font-medium">Email</label>
          <div className="relative">
            <FiMail className="absolute left-3 top-3 text-gray-400" />
            <input
              type="email"
              className="w-full py-2 pl-10 pr-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
          </div>
        </div>
        <div className="flex flex-col gap-2">
          <label className="text-gray-700 dark:text-gray-200 font-medium">Mot de passe</label>
          <div className="relative">
            <FiLock className="absolute left-3 top-3 text-gray-400" />
            <input
              type="password"
              className="w-full py-2 pl-10 pr-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              minLength={8}
            />
          </div>
        </div>
        <button
          type="submit"
          className="w-full py-3 rounded-lg bg-blue-600 text-white font-bold text-lg hover:bg-blue-700 transition-colors disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Création du compte..." : "Créer un compte"}
        </button>
        <div className="text-center text-gray-500 dark:text-gray-400 text-sm">
          Déjà un compte ? <a href="/login" className="text-blue-600 hover:underline">Se connecter</a>
        </div>
      </form>
    </div>
  );
} 