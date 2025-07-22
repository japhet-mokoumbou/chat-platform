import { useEffect, useState } from "react";
import { useRef } from "react";
import { useLocation } from "react-router-dom";

const API_URL = "http://localhost:8080/api";

export default function Conversations() {
  const [contacts, setContacts] = useState([]);
  const [groups, setGroups] = useState([]);
  const [selectedConv, setSelectedConv] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [userId, setUserId] = useState(null);
  const [file, setFile] = useState(null);
  const fileInputRef = useRef();

  const token = localStorage.getItem("token");
  const location = useLocation();

  // Récupérer l'ID utilisateur connecté au montage
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

  // Charger contacts et groupes au montage
  useEffect(() => {
    const fetchContacts = async () => {
      const res = await fetch(`${API_URL}/contacts`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setContacts(data.contacts || []);
    };
    const fetchGroups = async () => {
      const res = await fetch(`${API_URL}/groups`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const data = await res.json();
      setGroups(data.groups || []);
    };
    fetchContacts();
    fetchGroups();
  }, [token]);

  // Charger les messages quand une conversation est sélectionnée
  useEffect(() => {
    if (!selectedConv) return;
    setMessages([]);
    setPage(0);
    setHasMore(true);
    loadMessages(0, true);
    // eslint-disable-next-line
  }, [selectedConv]);

  // Marquer comme livré/lu les messages reçus non livrés/non lus
  useEffect(() => {
    if (!selectedConv || !userId || messages.length === 0) return;
    messages.forEach(async (msg) => {
      if (msg.receiverId === userId) return; // Ne pas marquer ses propres messages
      if (!msg.delivered) {
        await fetch(`${API_URL}/messages/${msg.id}/delivered`, {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        });
      }
      if (!msg.read) {
        await fetch(`${API_URL}/messages/${msg.id}/read`, {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        });
      }
    });
    // eslint-disable-next-line
  }, [messages, selectedConv, userId]);

  // Sélection automatique d'une conversation via l'URL
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const contactId = params.get("contactId");
    const groupId = params.get("groupId");
    // On attend que contacts et groups soient chargés
    if (contacts.length === 0 && groups.length === 0) return;
    if (contactId) {
      const conv = contacts.find(c => String(c.contactUserId) === String(contactId) || String(c.id) === String(contactId));
      if (conv) setSelectedConv({
        id: conv.id,
        name: conv.username || conv.alias,
        type: "private",
        contactUserId: conv.contactUserId,
        lastMessage: conv.lastMessage || "",
      });
    } else if (groupId) {
      const group = groups.find(g => String(g.id) === String(groupId));
      if (group) setSelectedConv({
        id: group.id,
        name: group.name,
        type: "group",
        lastMessage: group.lastMessage || "",
      });
    }
  }, [location.search, contacts, groups]);

  // Fonction pour charger les messages (pagination)
  const loadMessages = async (pageToLoad = page, reset = false) => {
    if (!selectedConv) return;
    setLoading(true);
    let url = "";
    if (selectedConv.type === "private") {
      url = `${API_URL}/messages/between?user1=${userId}&user2=${selectedConv.contactUserId}`;
    } else if (selectedConv.type === "group") {
      url = `${API_URL}/messages/group/${selectedConv.id}/paged?page=${pageToLoad}&size=20`;
    }
    const res = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await res.json();
    let newMessages = [];
    if (selectedConv.type === "private") {
      newMessages = data;
    } else {
      newMessages = data.content || [];
    }
    setMessages((prev) => reset ? newMessages : [...newMessages, ...prev]);
    setHasMore(selectedConv.type === "private" ? false : !data.last);
    setLoading(false);
  };

  // Gestion de l'envoi de message (texte uniquement pour l'instant)
  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || !selectedConv) return;
    setLoading(true);
    const body = {
      receiverId: selectedConv.type === "private" ? selectedConv.contactUserId : null,
      groupId: selectedConv.type === "group" ? selectedConv.id : null,
      content: input,
      type: "text",
    };
    await fetch(`${API_URL}/messages`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(body),
    });
    setInput("");
    loadMessages(0, true);
    setPage(0);
    setHasMore(true);
    setLoading(false);
  };

  // Gestion de l'upload de fichier
  const handleFileChange = async (e) => {
    const selected = e.target.files[0];
    if (!selected) return;
    setLoading(true);
    setFile(selected);
    // Upload du fichier
    const formData = new FormData();
    formData.append("file", selected);
    const res = await fetch(`${API_URL}/messages/upload`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: formData,
    });
    const data = await res.json();
    if (data.error) {
      alert("Erreur upload: " + data.error);
      setLoading(false);
      setFile(null);
      return;
    }
    // Envoi du message fichier
    const body = {
      receiverId: selectedConv.type === "private" ? selectedConv.contactUserId : null,
      groupId: selectedConv.type === "group" ? selectedConv.id : null,
      content: selected.name,
      type: "file",
    };
    const fileMeta = [
      data.filePath,
      data.mimeType,
      data.fileSize,
      data.width,
      data.height,
      data.duration,
      data.thumbnailPath
    ].join('|');
    await fetch(`${API_URL}/messages/send-file?fileMeta=${encodeURIComponent(fileMeta)}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(body),
    });
    setFile(null);
    setInput("");
    loadMessages(0, true);
    setPage(0);
    setHasMore(true);
    setLoading(false);
  };

  // Scroll infini (pagination)
  const handleLoadMore = () => {
    if (hasMore && !loading) {
      setPage((p) => {
        const next = p + 1;
        loadMessages(next);
        return next;
      });
    }
  };

  // Fusionner contacts et groupes pour la liste de conversations
  const conversations = [
    ...contacts.map((c) => ({
      id: c.id,
      name: c.username || c.alias,
      type: "private",
      contactUserId: c.contactUserId,
      lastMessage: c.lastMessage || "",
    })),
    ...groups.map((g) => ({
      id: g.id,
      name: g.name,
      type: "group",
      lastMessage: g.lastMessage || "",
    })),
  ];

  return (
    <div className="flex h-[80vh] bg-[#e5ddd5] rounded-xl shadow-lg overflow-hidden">
      {/* Liste des conversations */}
      <aside className="w-80 bg-white border-r border-[#ece5dd] p-0 flex flex-col">
        <div className="px-6 py-4 border-b border-[#ece5dd] bg-[#f7f9fa]">
          <h2 className="text-xl font-bold text-[#075e54]">Conversations</h2>
        </div>
        <ul className="flex-1 overflow-y-auto divide-y divide-[#f0f0f0]">
          {conversations.map((conv) => (
            <li
              key={conv.type + "-" + conv.id}
              className={`px-6 py-4 cursor-pointer flex items-center justify-between transition-colors select-none ${selectedConv && selectedConv.id === conv.id && selectedConv.type === conv.type ? "bg-[#e5f6e5] text-[#075e54]" : "hover:bg-[#f7f9fa]"}`}
              onClick={() => setSelectedConv(conv)}
            >
              <span className="font-semibold truncate">{conv.name}</span>
            </li>
          ))}
        </ul>
      </aside>
      {/* Zone de messages */}
      <section className="flex-1 flex flex-col h-full bg-[#f7f9fa]">
        <div className="flex-1 overflow-y-auto p-6 flex flex-col-reverse">
          <ul className="space-y-4 flex flex-col-reverse">
            {messages.map((msg) => (
              <li key={msg.id} className={`flex ${msg.senderId === userId ? "justify-end" : "justify-start"}`}>
                <div className={`max-w-lg px-4 py-2 rounded-2xl shadow ${msg.senderId === userId ? "bg-[#dcf8c6] text-[#075e54]" : "bg-white text-gray-900"}`}>
                  {/* Afficher le nom de l'éditeur pour les messages de groupe, sauf pour ses propres messages */}
                  {selectedConv?.type === "group" && msg.senderId !== userId && (
                    <div className="text-xs font-semibold text-[#25d366] mb-1">{msg.senderUsername || msg.senderId}</div>
                  )}
                  {msg.type === "file" ? (
                    <div>
                      {msg.mimeType && msg.mimeType.startsWith("image/") && msg.thumbnailPath ? (
                        <img src={`http://localhost:8080/${msg.thumbnailPath.replace(/^[.\\/]+/, "")}`} alt="miniature" className="w-32 h-32 object-cover rounded mb-2" />
                      ) : (
                        <span className="inline-block w-8 h-8 bg-gray-300 rounded mr-2 align-middle" title="Fichier joint">
                          <svg className="w-6 h-6 m-auto text-gray-600" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l7.07-7.07a4 4 0 00-5.656-5.657l-7.07 7.07a6 6 0 108.485 8.485L19 13" /></svg>
                        </span>
                      )}
                    </div>
                  ) : (
                    <span>{msg.content}</span>
                  )}
                </div>
              </li>
            ))}
          </ul>
        </div>
        {/* Zone de saisie */}
        <form onSubmit={handleSend} className="flex items-center gap-2 p-4 border-t border-[#ece5dd] bg-white">
          <button type="button" onClick={() => fileInputRef.current.click()} className="p-2 rounded-full hover:bg-[#e5ddd5] text-[#25d366]">
            <svg width="24" height="24" fill="none" viewBox="0 0 24 24"><path stroke="#25d366" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M21.44 11.05l-9.19 9.19a5 5 0 01-7.07-7.07l9.19-9.19a3.5 3.5 0 014.95 4.95l-9.19 9.19a2 2 0 01-2.83-2.83l8.49-8.49"/></svg>
          </button>
          <input
            type="file"
            ref={fileInputRef}
            className="hidden"
            onChange={handleFileChange}
          />
          <input
            type="text"
            className="flex-1 py-2 px-4 rounded-full bg-[#f7f9fa] border border-[#ece5dd] focus:outline-none focus:ring-2 focus:ring-[#25d366] text-[#075e54]"
            placeholder="Écrire un message..."
            value={input}
            onChange={e => setInput(e.target.value)}
            disabled={!selectedConv}
          />
          <button
            type="submit"
            className="p-2 rounded-full bg-[#25d366] text-white hover:bg-[#20ba5a] transition-colors disabled:opacity-60 shadow"
            disabled={!input.trim() || !selectedConv || loading}
          >
            <svg width="24" height="24" fill="none" viewBox="0 0 24 24"><path stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M22 2L11 13"/><path stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M22 2L15 22l-4-9-9-4 20-7z"/></svg>
          </button>
        </form>
      </section>
    </div>
  );
} 