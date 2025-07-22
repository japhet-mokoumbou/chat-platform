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
    <div className="min-h-screen flex items-center justify-center bg-[#e5ddd5]">
      <div className="w-full max-w-md flex flex-col items-center">
        {/* Logo WhatsApp style */}
        <div className="mb-6 flex flex-col items-center">
          <div className="bg-[#25d366] rounded-full p-4 shadow-lg mb-2">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="24" cy="24" r="24" fill="#25d366"/>
              <path d="M24 12C17.373 12 12 17.373 12 24c0 2.21.603 4.277 1.65 6.05L12 36l6.22-1.627A11.93 11.93 0 0024 36c6.627 0 12-5.373 12-12s-5.373-12-12-12zm0 21.6c-2.01 0-3.96-.59-5.6-1.7l-.4-.25-3.7.97.99-3.6-.26-.41A9.57 9.57 0 0114.4 24c0-5.3 4.3-9.6 9.6-9.6s9.6 4.3 9.6 9.6-4.3 9.6-9.6 9.6z" fill="#fff"/>
            </svg>
          </div>
          <h2 className="text-3xl font-bold text-[#075e54] text-center">Connexion</h2>
        </div>
        <form onSubmit={handleSubmit} className="bg-white shadow-xl rounded-2xl p-8 w-full flex flex-col gap-6 animate-fade-in border border-[#ece5dd]">
          {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded text-center">{error}</div>}
          <div className="flex flex-col gap-2">
            <label className="text-[#075e54] font-medium">Nom d'utilisateur</label>
            <input
              type="text"
              className="w-full py-2 px-4 rounded-lg bg-[#f7f9fa] text-gray-800 border border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
              autoFocus
              placeholder="Nom d'utilisateur ou email"
              minLength={3}
              maxLength={50}
            />
          </div>
          <div className="flex flex-col gap-2">
            <label className="text-[#075e54] font-medium">Mot de passe</label>
            <input
              type="password"
              className="w-full py-2 px-4 rounded-lg bg-[#f7f9fa] text-gray-800 border border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
              placeholder="Mot de passe"
              minLength={8}
            />
          </div>
          <button
            type="submit"
            className="w-full py-3 rounded-lg bg-[#25d366] text-white font-bold text-lg hover:bg-[#20ba5a] transition-colors disabled:opacity-60 shadow-md"
            disabled={loading}
          >
            {loading ? "Connexion..." : "Se connecter"}
          </button>
          <div className="text-center text-[#4a4a4a] text-sm">
            Pas encore de compte ? <Link to="/register" className="text-[#25d366] hover:underline">Créer un compte</Link>
          </div>
        </form>
      </div>
    </div>
  );
} 