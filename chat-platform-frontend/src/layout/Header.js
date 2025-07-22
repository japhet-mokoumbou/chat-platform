import { useNavigate } from "react-router-dom";
import { useState } from "react";

export default function Header() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [showResults, setShowResults] = useState(false);

  // Recherche globale sur contacts, groupes, conversations
  const handleSearch = async (e) => {
    const q = e.target.value;
    setQuery(q);
    if (!q.trim()) {
      setResults([]);
      setShowResults(false);
      return;
    }
    // Appels API en parallèle
    const token = localStorage.getItem("token");
    const [contactsRes, groupsRes, convosRes] = await Promise.all([
      fetch("http://localhost:8080/api/contacts", { headers: { Authorization: `Bearer ${token}` } }),
      fetch("http://localhost:8080/api/groups", { headers: { Authorization: `Bearer ${token}` } }),
      fetch("http://localhost:8080/api/conversations", { headers: { Authorization: `Bearer ${token}` } }).catch(() => ({ ok: false, json: async () => [] })),
    ]);
    const contactsData = contactsRes.ok ? await contactsRes.json() : { contacts: [] };
    const groupsData = groupsRes.ok ? await groupsRes.json() : { groups: [] };
    // conversations endpoint optionnel, fallback sur contacts+groupes
    let conversations = [];
    if (convosRes && convosRes.ok) {
      const data = await convosRes.json();
      conversations = data.conversations || [];
    }
    // Filtrage local
    const qLower = q.toLowerCase();
    const contactResults = (contactsData.contacts || []).filter(c => (c.alias || c.username || c.email || "").toLowerCase().includes(qLower));
    const groupResults = (groupsData.groups || []).filter(g => (g.name || "").toLowerCase().includes(qLower));
    const convResults = conversations.filter(c => (c.name || "").toLowerCase().includes(qLower));
    setResults([
      ...contactResults.map(c => ({ type: "contact", label: c.alias || c.username || c.email, id: c.id })),
      ...groupResults.map(g => ({ type: "group", label: g.name, id: g.id })),
      ...convResults.map(c => ({ type: "conversation", label: c.name, id: c.id })),
    ]);
    setShowResults(true);
  };

  const handleSelect = (item) => {
    setShowResults(false);
    setQuery("");
    if (item.type === "contact") navigate(`/conversations?contactId=${item.contactUserId || item.id}`);
    else if (item.type === "group") navigate(`/conversations?groupId=${item.id}`);
    else if (item.type === "conversation") navigate(`/conversations?convId=${item.id}`);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };
  return (
    <header className="flex items-center justify-between h-20 px-6 bg-[#075e54] border-b border-[#25d366] sticky top-0 z-10 shadow">
      <div className="flex items-center gap-4 w-1/2 relative">
        <div className="relative w-full">
          <input
            type="text"
            placeholder="Rechercher une conversation, un contact, un groupe..."
            className="w-full py-2 pl-10 pr-4 rounded-lg bg-[#e5ddd5] text-[#075e54] placeholder-[#4a4a4a] focus:outline-none focus:ring-2 focus:ring-[#25d366] border border-[#25d366]"
            value={query}
            onChange={handleSearch}
            onFocus={() => query && setShowResults(true)}
            onBlur={() => setTimeout(() => setShowResults(false), 150)}
          />
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-[#25d366]">
            <svg width="20" height="20" fill="none" viewBox="0 0 24 24"><path stroke="#25d366" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M21 21l-4.35-4.35M11 19a8 8 0 100-16 8 8 0 000 16z"/></svg>
          </span>
          {showResults && results.length > 0 && (
            <ul className="absolute left-0 right-0 top-12 bg-white border border-[#25d366] rounded-lg shadow-lg z-50 max-h-60 overflow-y-auto">
              {results.map((item, idx) => (
                <li
                  key={item.type + '-' + item.id + '-' + idx}
                  className="px-4 py-2 hover:bg-[#e5ddd5] cursor-pointer text-[#075e54]"
                  onMouseDown={() => handleSelect(item)}
                >
                  <span className="font-bold mr-2">{item.type === 'contact' ? '👤' : item.type === 'group' ? '👥' : '💬'}</span>
                  {item.label}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
      <div className="flex items-center gap-6">
        <button
          onClick={handleLogout}
          className="px-4 py-2 rounded bg-[#25d366] text-white hover:bg-[#20ba5a] transition-colors font-semibold shadow"
          title="Se déconnecter"
        >
          Déconnexion
        </button>
        <div className="w-10 h-10 rounded-full bg-[#e5ddd5] flex items-center justify-center text-xl font-bold text-[#075e54] border-2 border-[#25d366]">
          {/* Avatar utilisateur (initiales ou image) */}
          U
        </div>
      </div>
    </header>
  );
} 