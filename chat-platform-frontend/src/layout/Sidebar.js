import { Link } from "react-router-dom";
import { useState, useContext } from "react";
import { FiChevronLeft, FiChevronRight } from "react-icons/fi";
import { SidebarContext } from "../App";

export default function Sidebar() {
  const { sidebarOpen, setSidebarOpen } = useContext(SidebarContext);
  const open = sidebarOpen;
  return (
    <aside className={`fixed left-0 top-0 h-screen bg-[#075e54] flex flex-col transition-all duration-300 z-30 shadow-lg ${open ? 'w-72' : 'w-20'}`}>
      <div className="flex items-center justify-between h-20 px-4 border-b border-[#25d366]">
        <span className={`text-2xl font-bold text-white tracking-tight flex items-center gap-2 transition-all duration-300 ${open ? 'block' : 'hidden md:block'}`}>
          <span className="bg-[#25d366] rounded-full p-1"><svg width="28" height="28" viewBox="0 0 48 48"><circle cx="24" cy="24" r="24" fill="#25d366"/><path d="M24 12C17.373 12 12 17.373 12 24c0 2.21.603 4.277 1.65 6.05L12 36l6.22-1.627A11.93 11.93 0 0024 36c6.627 0 12-5.373 12-12s-5.373-12-12-12zm0 21.6c-2.01 0-3.96-.59-5.6-1.7l-.4-.25-3.7.97.99-3.6-.26-.41A9.57 9.57 0 0114.4 24c0-5.3 4.3-9.6 9.6-9.6s9.6 4.3 9.6 9.6-4.3 9.6-9.6 9.6z" fill="#fff"/></svg></span>
          {open && 'ChatESP'}
        </span>
        <button onClick={() => setSidebarOpen(o => !o)} className="ml-2 p-2 rounded-full hover:bg-[#25d366]/20 text-white transition-colors">
          {open ? <FiChevronLeft size={24} /> : <FiChevronRight size={24} />}
        </button>
      </div>
      <nav className="flex-1 flex flex-col gap-2 mt-8">
        <Link to="/" className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-white hover:bg-[#25d366] transition-all duration-200 ${!open ? 'justify-center px-0' : ''}`}>
          <span className="text-2xl">🏠</span>
          {open && 'Accueil'}
        </Link>
        <Link to="/conversations" className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-white hover:bg-[#25d366] transition-all duration-200 ${!open ? 'justify-center px-0' : ''}`}>
          <span className="text-2xl">💬</span>
          {open && 'Conversations'}
        </Link>
        <Link to="/contacts" className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-white hover:bg-[#25d366] transition-all duration-200 ${!open ? 'justify-center px-0' : ''}`}>
          <span className="text-2xl">👥</span>
          {open && 'Contacts'}
        </Link>
        <Link to="/groups" className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-white hover:bg-[#25d366] transition-all duration-200 ${!open ? 'justify-center px-0' : ''}`}>
          <span className="text-2xl">👨‍👩‍👧‍👦</span>
          {open && 'Groupes'}
        </Link>
        <Link to="/profile" className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-white hover:bg-[#25d366] transition-all duration-200 ${!open ? 'justify-center px-0' : ''}`}>
          <span className="text-2xl">🙍‍♂️</span>
          {open && 'Profil'}
        </Link>
        <Link to="/settings" className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg text-lg font-medium text-white hover:bg-[#25d366] transition-all duration-200 ${!open ? 'justify-center px-0' : ''}`}>
          <span className="text-2xl">⚙️</span>
          {open && 'Paramètres'}
        </Link>
      </nav>
    </aside>
  );
} 