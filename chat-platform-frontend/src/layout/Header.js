import { useNavigate } from "react-router-dom";

export default function Header() {
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };
  return (
    <header className="flex items-center justify-between h-20 px-6 bg-white dark:bg-gray-950 border-b border-gray-200 dark:border-gray-800 sticky top-0 z-10">
      <div className="flex items-center gap-4 w-1/2">
        <div className="relative w-full">
          <input
            type="text"
            placeholder="Rechercher une conversation, un contact..."
            className="w-full py-2 pl-10 pr-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>
      <div className="flex items-center gap-6">
        <button
          onClick={handleLogout}
          className="px-4 py-2 rounded bg-red-500 text-white hover:bg-red-600 transition-colors font-semibold"
          title="Se déconnecter"
        >
          Déconnexion
        </button>
        <div className="w-10 h-10 rounded-full bg-blue-200 dark:bg-blue-800 flex items-center justify-center text-xl font-bold text-blue-700 dark:text-blue-200">
          {/* Avatar utilisateur (initiales ou image) */}
          U
        </div>
      </div>
    </header>
  );
} 