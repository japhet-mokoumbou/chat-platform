import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ usernameOrEmail: username, password })
      });
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.error || "Nom d'utilisateur ou mot de passe incorrect");
      }
      localStorage.setItem("token", data.token);
      navigate("/");
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-blue-300 dark:from-gray-900 dark:to-gray-800">
      <form onSubmit={handleSubmit} className="bg-white dark:bg-gray-950 shadow-xl rounded-2xl p-8 w-full max-w-md flex flex-col gap-6 animate-fade-in">
        <h2 className="text-3xl font-bold text-blue-600 text-center mb-2">Connexion à ChatESP</h2>
        {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded text-center">{error}</div>}
        <div className="flex flex-col gap-2">
          <label className="text-gray-700 dark:text-gray-200 font-medium">Nom d'utilisateur</label>
          <input
            type="text"
            className="w-full py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            autoFocus
            placeholder="Nom d'utilisateur"
            minLength={3}
            maxLength={50}
          />
        </div>
        <div className="flex flex-col gap-2">
          <label className="text-gray-700 dark:text-gray-200 font-medium">Mot de passe</label>
          <input
            type="password"
            className="w-full py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            placeholder="Mot de passe"
            minLength={8}
          />
        </div>
        <button
          type="submit"
          className="w-full py-3 rounded-lg bg-blue-600 text-white font-bold text-lg hover:bg-blue-700 transition-colors disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Connexion..." : "Se connecter"}
        </button>
        <div className="text-center text-gray-500 dark:text-gray-400 text-sm">
          Pas encore de compte ? <Link to="/register" className="text-blue-600 hover:underline">Créer un compte</Link>
        </div>
      </form>
    </div>
  );
} 