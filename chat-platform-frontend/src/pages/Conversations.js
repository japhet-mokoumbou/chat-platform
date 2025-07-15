import { useEffect, useState } from "react";
import { useRef } from "react";

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
    <div className="flex h-[80vh] bg-white dark:bg-gray-950 rounded-xl shadow-lg overflow-hidden">
      {/* Liste des conversations */}
      <aside className="w-72 bg-gray-50 dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 p-4 overflow-y-auto">
        <h2 className="text-xl font-bold mb-4 text-blue-600">Conversations</h2>
        <ul className="space-y-2">
          {conversations.map((conv) => (
            <li
              key={conv.type + "-" + conv.id}
              className={`p-3 rounded-lg cursor-pointer flex items-center justify-between transition-colors ${selectedConv && selectedConv.id === conv.id && selectedConv.type === conv.type ? "bg-blue-100 dark:bg-blue-800 text-blue-800 dark:text-blue-100" : "hover:bg-gray-100 dark:hover:bg-gray-800"}`}
              onClick={() => setSelectedConv(conv)}
            >
              <span className="font-semibold">{conv.name}</span>
            </li>
          ))}
        </ul>
      </aside>
      {/* Zone de messages */}
      <section className="flex-1 flex flex-col h-full">
        <div className="flex-1 overflow-y-auto p-6 bg-gray-50 dark:bg-gray-900 flex flex-col-reverse">
          <ul className="space-y-4 flex flex-col-reverse">
            {messages.map((msg) => (
              <li key={msg.id} className={`flex ${msg.senderId === userId ? "justify-end" : "justify-start"}`}>
                <div className={`max-w-lg px-4 py-2 rounded-2xl shadow-md ${msg.senderId === userId ? "bg-blue-600 text-white" : "bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100"}`}>
                  {/* Afficher le nom de l'éditeur pour les messages de groupe, sauf pour ses propres messages */}
                  {selectedConv?.type === "group" && msg.senderId !== userId && (
                    <div className="text-xs font-semibold text-blue-600 dark:text-blue-300 mb-1">{msg.senderUsername || msg.senderId}</div>
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
                      <div className="text-sm font-semibold mt-1">{msg.content}</div>
                      <a href={`http://localhost:8080/${msg.filePath ? msg.filePath.replace(/^[.\\/]+/, "") : "#"}`} target="_blank" rel="noopener noreferrer" className="text-xs text-blue-500 underline">Télécharger / Voir</a>
                    </div>
                  ) : (
                    <div>{msg.content}</div>
                  )}
                  <span className="text-xs text-gray-300">{msg.sentAt ? new Date(msg.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ""}</span>
                  {msg.senderId === userId && (
                    <span title={msg.read ? "Lu" : msg.delivered ? "Livré" : "Envoyé"}>
                      {msg.read ? (
                        <span className="text-blue-400">✓✓</span>
                      ) : msg.delivered ? (
                        <span className="text-gray-400">✓</span>
                      ) : (
                        <span className="text-gray-300">✓</span>
                      )}
                    </span>
                  )}
                </div>
              </li>
            ))}
          </ul>
          {hasMore && (
            <button onClick={handleLoadMore} className="mx-auto mt-2 px-4 py-1 rounded bg-blue-100 dark:bg-blue-800 text-blue-700 dark:text-blue-200 text-xs">Charger plus</button>
          )}
        </div>
        {/* Zone de saisie */}
        <form onSubmit={handleSend} className="p-4 border-t border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950 flex items-center gap-2">
          <button
            type="button"
            className="p-2 rounded-full bg-gray-200 dark:bg-gray-800 text-gray-600 dark:text-gray-200 hover:bg-gray-300 dark:hover:bg-gray-700 transition-colors"
            onClick={() => fileInputRef.current.click()}
            disabled={loading}
            title="Envoyer un fichier"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l7.07-7.07a4 4 0 00-5.656-5.657l-7.07 7.07a6 6 0 108.485 8.485L19 13" /></svg>
            <input
              type="file"
              ref={fileInputRef}
              className="hidden"
              onChange={handleFileChange}
              accept="image/*,application/pdf,video/*"
              disabled={loading}
            />
          </button>
          <input
            type="text"
            className="flex-1 py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Écrire un message..."
            value={input}
            onChange={e => setInput(e.target.value)}
            disabled={loading}
          />
          <button
            type="submit"
            className="p-2 rounded-full bg-blue-600 text-white hover:bg-blue-700 transition-colors ml-2"
            disabled={!input.trim() || loading}
            title="Envoyer"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" /></svg>
          </button>
        </form>
      </section>
    </div>
  );
} 