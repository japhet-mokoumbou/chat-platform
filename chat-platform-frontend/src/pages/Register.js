import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";

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
      const res = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password })
      });
      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.error || "Erreur d'inscription");
      }
      navigate("/login");
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
          <h2 className="text-3xl font-bold text-[#075e54] text-center">Créer un compte</h2>
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
              minLength={3}
              maxLength={50}
            />
          </div>
          <div className="flex flex-col gap-2">
            <label className="text-[#075e54] font-medium">Email</label>
            <input
              type="email"
              className="w-full py-2 px-4 rounded-lg bg-[#f7f9fa] text-gray-800 border border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
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
              minLength={8}
            />
          </div>
          <button
            type="submit"
            className="w-full py-3 rounded-lg bg-[#25d366] text-white font-bold text-lg hover:bg-[#20ba5a] transition-colors disabled:opacity-60 shadow-md"
            disabled={loading}
          >
            {loading ? "Création du compte..." : "Créer un compte"}
          </button>
          <div className="text-center text-[#4a4a4a] text-sm">
            Déjà un compte ? <Link to="/login" className="text-[#25d366] hover:underline">Se connecter</Link>
          </div>
        </form>
      </div>
    </div>
  );
} 