import { useEffect, useState } from "react";

const API_URL = "http://localhost:8080/api";

export default function Groups() {
  const [groups, setGroups] = useState([]);
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [groupName, setGroupName] = useState("");
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [message, setMessage] = useState("");
  const token = localStorage.getItem("token");
  const [userId, setUserId] = useState(null);

  // Charger groupes et contacts
  useEffect(() => {
    const fetchGroups = async () => {
      setLoading(true);
      const res = await fetch(`${API_URL}/groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.status === 401 || res.status === 403) {
        localStorage.removeItem("token");
        window.location.href = "/login";
        return;
      }
      const data = await res.json();
      setGroups(data.groups || []);
      setLoading(false);
    };
    const fetchContacts = async () => {
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
    };
    fetchGroups();
    fetchContacts();
  }, [token]);

  // Récupérer l'ID utilisateur connecté
  useEffect(() => {
    const fetchMe = async () => {
      const res = await fetch(`${API_URL}/test/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setUserId(data.id);
    };
    fetchMe();
  }, [token]);

  // Création de groupe
  const handleCreateGroup = async (e) => {
    e.preventDefault();
    setMessage("");
    if (!groupName.trim() || selectedMembers.length === 0) {
      setMessage("Nom et membres obligatoires");
      return;
    }
    setLoading(true);
    const res = await fetch(`${API_URL}/groups`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ name: groupName, memberIds: selectedMembers }),
    });
    const data = await res.json();
    if (res.ok) {
      setMessage("Groupe créé !");
      setGroupName("");
      setSelectedMembers([]);
      // Rafraîchir la liste
      const res2 = await fetch(`${API_URL}/groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data2 = await res2.json();
      setGroups(data2.groups || []);
    } else {
      setMessage(data.error || "Erreur lors de la création");
    }
    setLoading(false);
  };

  const handleMemberToggle = (id) => {
    setSelectedMembers((prev) =>
      prev.includes(id) ? prev.filter((m) => m !== id) : [...prev, id]
    );
  };

  // Ajouter un membre à un groupe
  const handleAddMember = async (groupId, memberId) => {
    setLoading(true);
    setMessage("");
    const res = await fetch(`${API_URL}/groups/${groupId}/members`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ userId: memberId }),
    });
    const data = await res.json();
    if (res.ok) {
      setMessage("Membre ajouté !");
      // Rafraîchir la liste
      const res2 = await fetch(`${API_URL}/groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data2 = await res2.json();
      setGroups(data2.groups || []);
    } else {
      setMessage(data.error || "Erreur lors de l'ajout du membre");
    }
    setLoading(false);
  };
  // Supprimer un membre d'un groupe
  const handleRemoveMember = async (groupId, memberId) => {
    setLoading(true);
    setMessage("");
    const res = await fetch(`${API_URL}/groups/${groupId}/members/${memberId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await res.json();
    if (res.ok) {
      setMessage("Membre supprimé !");
      // Rafraîchir la liste
      const res2 = await fetch(`${API_URL}/groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data2 = await res2.json();
      setGroups(data2.groups || []);
    } else {
      setMessage(data.error || "Erreur lors de la suppression du membre");
    }
    setLoading(false);
  };

  return (
    <div className="max-w-3xl mx-auto p-8 bg-[#e5ddd5] rounded-xl shadow-lg mt-8">
      <h2 className="text-2xl font-bold mb-6 text-[#075e54]">Mes groupes</h2>
      <form onSubmit={handleCreateGroup} className="mb-8 flex flex-col gap-4 bg-[#f7f9fa] p-4 rounded-lg">
        <div className="flex flex-col md:flex-row gap-4 items-center">
          <input
            type="text"
            placeholder="Nom du groupe"
            className="flex-1 py-2 px-4 rounded-lg bg-[#f7f9fa] text-[#075e54] border border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
            value={groupName}
            onChange={e => setGroupName(e.target.value)}
            disabled={loading}
          />
          <button
            type="submit"
            className="px-4 py-2 rounded bg-[#25d366] text-white font-semibold hover:bg-[#20ba5a] transition-colors shadow"
            disabled={loading || !groupName.trim() || selectedMembers.length === 0}
          >Créer le groupe</button>
        </div>
        <div className="flex flex-wrap gap-2">
          {contacts.map((c) => (
            <label key={c.id} className="flex items-center gap-2 bg-white px-3 py-1 rounded shadow text-sm cursor-pointer">
              <input
                type="checkbox"
                checked={selectedMembers.includes(c.contactUserId)}
                onChange={() => handleMemberToggle(c.contactUserId)}
                disabled={loading}
              />
              {c.alias ? `${c.alias} (${c.email})` : c.email}
            </label>
          ))}
        </div>
        {message && <div className="text-center text-[#25d366] text-sm mt-2">{message}</div>}
      </form>
      {loading ? (
        <div className="text-center text-[#075e54]">Chargement...</div>
      ) : groups.length === 0 ? (
        <div className="text-center text-gray-400">Aucun groupe pour le moment.</div>
      ) : (
        <ul className="divide-y divide-[#ece5dd]">
          {groups.map((g) => (
            <li key={g.id} className="py-4">
              <div className="font-semibold text-lg text-[#075e54]">{g.name}</div>
              <div className="text-sm text-gray-500">Créé par : {g.creatorUsername || g.creatorId}</div>
              <div className="text-xs text-gray-400 mt-1">Membres : {g.memberUsernames && g.memberUsernames.length ? (
                <ul className="flex flex-wrap gap-2 mt-1">
                  {g.memberUsernames.map((username, idx) => (
                    <li key={username} className="flex items-center gap-1 bg-[#f7f9fa] px-2 py-1 rounded">
                      <span>{username}</span>
                      {userId === g.creatorId && g.members[idx] !== userId && (
                        <button
                          onClick={() => handleRemoveMember(g.id, g.members[idx])}
                          className="text-xs text-red-500 ml-1 hover:underline"
                          disabled={loading}
                          title="Retirer ce membre"
                        >✕</button>
                      )}
                    </li>
                  ))}
                </ul>
              ) : "aucun"}</div>
              {userId === g.creatorId && (
                <div className="mt-2 flex flex-wrap gap-2">
                  {contacts.filter(c => !g.members.includes(c.contactUserId)).length === 0 ? (
                    <span className="text-xs text-gray-400">Tous vos contacts sont déjà membres</span>
                  ) : contacts.filter(c => !g.members.includes(c.contactUserId)).map(c => (
                    <button
                      key={c.id}
                      onClick={() => handleAddMember(g.id, c.contactUserId)}
                      className="px-3 py-1 rounded bg-[#25d366] text-white text-xs hover:bg-[#20ba5a] shadow"
                      disabled={loading}
                      title={`Ajouter ${c.alias ? c.alias : c.email}`}
                    >Ajouter {c.alias ? `${c.alias} (${c.email})` : c.email}</button>
                  ))}
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
} 