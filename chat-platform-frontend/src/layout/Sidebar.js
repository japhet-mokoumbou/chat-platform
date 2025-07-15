import { Link } from "react-router-dom";

export default function Sidebar() {
  return (
    <aside className="w-20 md:w-64 h-screen bg-white dark:bg-gray-950 border-r border-gray-200 dark:border-gray-800 flex flex-col transition-all duration-300 z-30">
      <div className="flex items-center justify-center md:justify-start h-20 px-4 border-b border-gray-100 dark:border-gray-800">
        <span className="hidden md:block text-2xl font-bold text-blue-600 tracking-tight">ChatESP</span>
      </div>
      <nav className="flex-1 flex flex-col gap-2 mt-8">
        <Link to="/" className="flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 transition-colors">
          Accueil
        </Link>
        <Link to="/conversations" className="flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 transition-colors">
          Conversations
        </Link>
        <Link to="/contacts" className="flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 transition-colors">
          Contacts
        </Link>
        <Link to="/groups" className="flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 transition-colors">
          Groupes
        </Link>
        <Link to="/profile" className="flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 transition-colors">
          Profil
        </Link>
        <Link to="/settings" className="flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 transition-colors">
          Paramètres
        </Link>
      </nav>
    </aside>
  );
} 