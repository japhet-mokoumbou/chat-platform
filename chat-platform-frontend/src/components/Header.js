import { FiBell, FiSearch } from "react-icons/fi";

export default function Header() {
  return (
    <header className="flex items-center justify-between h-20 px-6 bg-white dark:bg-gray-950 border-b border-gray-200 dark:border-gray-800">
      <div className="flex items-center gap-4 w-1/2">
        <div className="relative w-full">
          <input
            type="text"
            placeholder="Rechercher une conversation, un contact..."
            className="w-full py-2 pl-10 pr-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <FiSearch className="absolute left-3 top-2.5 text-gray-400 text-xl" />
        </div>
      </div>
      <div className="flex items-center gap-6">
        <button className="relative">
          <FiBell className="text-2xl text-gray-500 dark:text-gray-300" />
          <span className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full border-2 border-white dark:border-gray-950"></span>
        </button>
        <div className="w-10 h-10 rounded-full bg-blue-200 dark:bg-blue-800 flex items-center justify-center text-xl font-bold text-blue-700 dark:text-blue-200">
          {/* Avatar utilisateur (initiales ou image) */}
          U
        </div>
      </div>
    </header>
  );
} 