import { Link, useLocation } from "react-router-dom";
import { useState } from "react";
import { FiMessageSquare, FiUsers, FiUser, FiSettings, FiLogOut, FiHash } from "react-icons/fi";
import logo from "../logo.svg";

const navItems = [
  { to: "/conversations", icon: <FiMessageSquare />, label: "Conversations" },
  { to: "/groups", icon: <FiHash />, label: "Groupes" },
  { to: "/contacts", icon: <FiUsers />, label: "Contacts" },
  { to: "/profile", icon: <FiUser />, label: "Profil" },
  { to: "/settings", icon: <FiSettings />, label: "Paramètres" },
];

export default function Sidebar() {
  const location = useLocation();
  const [open, setOpen] = useState(true);

  return (
    <aside className={`bg-white dark:bg-gray-950 border-r border-gray-200 dark:border-gray-800 flex flex-col h-screen w-20 md:w-64 transition-all duration-300 ${open ? "" : "w-20"}`}> 
      <div className="flex items-center justify-center md:justify-start h-20 px-4 border-b border-gray-100 dark:border-gray-800">
        <img src={logo} alt="ChatESP" className="w-10 h-10 mr-0 md:mr-3" />
        <span className="hidden md:block text-2xl font-bold text-blue-600 tracking-tight">ChatESP</span>
      </div>
      <nav className="flex-1 flex flex-col gap-2 mt-8">
        {navItems.map((item) => (
          <Link
            key={item.to}
            to={item.to}
            className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium transition-colors duration-200
              ${location.pathname.startsWith(item.to) ? "bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300" : "text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-800"}`}
          >
            <span className="text-2xl">{item.icon}</span>
            <span className="hidden md:inline">{item.label}</span>
          </Link>
        ))}
      </nav>
      <div className="mt-auto mb-6 px-4">
        <button className="flex items-center gap-3 w-full px-4 py-2 rounded-lg text-lg font-medium text-red-600 hover:bg-red-50 dark:hover:bg-red-900 transition-colors">
          <FiLogOut className="text-2xl" />
          <span className="hidden md:inline">Déconnexion</span>
        </button>
      </div>
    </aside>
  );
} 