import { useEffect, useState } from "react";

const API_URL = "http://localhost:8080/api";

export default function Contacts() {
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [addId, setAddId] = useState("");
  const [addAlias, setAddAlias] = useState("");
  const [addEmail, setAddEmail] = useState("");
  const [message, setMessage] = useState("");
  const [editId, setEditId] = useState(null);
  const [editAlias, setEditAlias] = useState("");
  const token = localStorage.getItem("token");

  const fetchContacts = async () => {
    setLoading(true);
    const res = await fetch(`${API_URL}/contacts`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.status === 401 || res.status === 403) {
      localStorage.removeItem("token");
      window.location.href = "/login";
      return;
    }
    const data = await res.json();
    setContacts(data.contacts || []);
    setLoading(false);
  };

  useEffect(() => {
    fetchContacts();
    // eslint-disable-next-line
  }, [token]);

  // Ajouter un contact
  const handleAdd = async (e) => {
    e.preventDefault();
    setMessage("");
    if (!addEmail.trim()) return;
    setLoading(true);
    const res = await fetch(`${API_URL}/contacts`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ email: addEmail, alias: addAlias }),
    });
    const data = await res.json();
    if (res.ok) {
      setMessage("Contact ajouté !");
      setAddEmail("");
      setAddAlias("");
      fetchContacts();
    } else {
      setMessage(JSON.stringify(data));
    }
    setLoading(false);
  };

  // Supprimer un contact
  const handleDelete = async (contactId) => {
    setLoading(true);
    setMessage("");
    const res = await fetch(`${API_URL}/contacts/${contactId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) {
      setMessage("Contact supprimé");
      fetchContacts();
    } else {
      const data = await res.json();
      setMessage(data.error || "Erreur lors de la suppression");
    }
    setLoading(false);
  };

  // Modifier l'alias
  const handleEdit = (contact) => {
    setEditId(contact.id);
    setEditAlias(contact.alias || "");
  };
  const handleEditSave = async (contactId) => {
    setLoading(true);
    setMessage("");
    const res = await fetch(`${API_URL}/contacts/${contactId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ alias: editAlias }),
    });
    const data = await res.json();
    if (res.ok) {
      setMessage("Alias modifié !");
      setEditId(null);
      setEditAlias("");
      fetchContacts();
    } else {
      setMessage(data.error || "Erreur lors de la modification");
    }
    setLoading(false);
  };
  const handleEditCancel = () => {
    setEditId(null);
    setEditAlias("");
  };

  return (
    <div className="max-w-2xl mx-auto p-8 bg-white dark:bg-gray-900 rounded-xl shadow-lg mt-8">
      <h2 className="text-2xl font-bold mb-6 text-blue-600">Mes contacts</h2>
      <form onSubmit={handleAdd} className="flex gap-2 mb-6">
        <input
          type="email"
          placeholder="Email du contact à ajouter"
          className="flex-1 py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-100 focus:outline-none"
          value={addEmail}
          onChange={e => setAddEmail(e.target.value)}
          disabled={loading}
        />
        <input
          type="text"
          placeholder="Alias (optionnel)"
          className="py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-100 focus:outline-none"
          value={addAlias}
          onChange={e => setAddAlias(e.target.value)}
          disabled={loading}
        />
        <button
          type="submit"
          className="px-4 py-2 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 transition-colors"
          disabled={loading || !addEmail.trim()}
        >Ajouter</button>
      </form>
      {message && <div className="mb-4 text-center text-sm text-blue-600">{message}</div>}
      {loading ? (
        <div className="text-center text-gray-500">Chargement...</div>
      ) : contacts.length === 0 ? (
        <div className="text-center text-gray-400">Aucun contact pour le moment.</div>
      ) : (
        <ul className="divide-y divide-gray-200 dark:divide-gray-800">
          {contacts.map((c) => (
            <li key={c.id} className="py-4 flex items-center justify-between">
              <div>
                {editId === c.id ? (
                  <div className="flex gap-2 items-center">
                    <input
                      type="text"
                      className="py-1 px-2 rounded bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-100 focus:outline-none"
                      value={editAlias}
                      onChange={e => setEditAlias(e.target.value)}
                      disabled={loading}
                    />
                    <button onClick={() => handleEditSave(c.id)} className="px-2 py-1 bg-blue-500 text-white rounded text-xs" disabled={loading}>Enregistrer</button>
                    <button onClick={handleEditCancel} className="px-2 py-1 bg-gray-300 text-gray-700 rounded text-xs ml-1">Annuler</button>
                  </div>
                ) : (
                  <div className="font-semibold text-lg flex gap-2 items-center">
                    {c.alias || <span className="italic text-gray-400">(aucun alias)</span>}
                    <button onClick={() => handleEdit(c)} className="ml-2 text-xs text-blue-500 underline">Modifier</button>
                  </div>
                )}
                <div className="text-sm text-gray-500">{c.email}</div>
              </div>
              <button
                onClick={() => handleDelete(c.id)}
                className="px-3 py-1 rounded bg-red-500 text-white text-sm hover:bg-red-600 transition-colors"
                disabled={loading}
                title="Supprimer ce contact"
              >Supprimer</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
} 