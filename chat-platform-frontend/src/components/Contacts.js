import { useState, useEffect } from "react";
import { FiUserPlus, FiTrash2, FiEdit2, FiSearch } from "react-icons/fi";
import api from "../api/api";

export default function Contacts() {
  const [contacts, setContacts] = useState([]);
  const [search, setSearch] = useState("");
  const [newContact, setNewContact] = useState("");

  // TODO: Charger les contacts (API)
  useEffect(() => {
    // api.getContacts().then(setContacts);
  }, []);

  const handleAdd = async () => {
    if (!newContact) return;
    // TODO: Ajouter le contact via API
    setNewContact("");
  };

  const handleDelete = async (id) => {
    // TODO: Supprimer le contact via API
  };

  return (
    <div className="p-8 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold mb-6 text-blue-600">Mes contacts</h2>
      <div className="flex gap-2 mb-4">
        <div className="relative flex-1">
          <input
            type="text"
            placeholder="Rechercher un contact..."
            className="w-full py-2 pl-10 pr-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <FiSearch className="absolute left-3 top-2.5 text-gray-400 text-xl" />
        </div>
        <input
          type="text"
          placeholder="Ajouter par nom d'utilisateur"
          className="py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={newContact}
          onChange={e => setNewContact(e.target.value)}
        />
        <button
          className="p-2 rounded-full bg-blue-600 text-white hover:bg-blue-700 transition-colors"
          onClick={handleAdd}
          title="Ajouter"
        >
          <FiUserPlus />
        </button>
      </div>
      <ul className="divide-y divide-gray-200 dark:divide-gray-800">
        {contacts.filter(c => c.username?.toLowerCase().includes(search.toLowerCase())).map((c) => (
          <li key={c.id} className="flex items-center justify-between py-3">
            <div>
              <span className="font-semibold text-gray-900 dark:text-gray-100">{c.username}</span>
              {c.alias && <span className="ml-2 text-gray-500">({c.alias})</span>}
            </div>
            <div className="flex gap-2">
              <button className="p-2 rounded-full bg-gray-100 dark:bg-gray-800 text-blue-600 hover:bg-blue-100 dark:hover:bg-blue-700" title="Éditer"><FiEdit2 /></button>
              <button className="p-2 rounded-full bg-red-100 dark:bg-red-900 text-red-600 hover:bg-red-200 dark:hover:bg-red-800" onClick={() => handleDelete(c.id)} title="Supprimer"><FiTrash2 /></button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
} 