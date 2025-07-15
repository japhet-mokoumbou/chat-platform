import { useState, useEffect, useRef } from "react";
import { FiSend, FiPaperclip, FiImage, FiFile, FiCheck, FiEdit2, FiTrash2 } from "react-icons/fi";
// import { useWebSocket } from "../services/websocket"; // à implémenter
import api from "../api/api";

export default function Conversations() {
  // États pour la liste des conversations, messages, sélection, etc.
  const [conversations, setConversations] = useState([]);
  const [selectedConv, setSelectedConv] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [file, setFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const fileInputRef = useRef();

  // TODO: Charger les conversations de l'utilisateur (API)
  useEffect(() => {
    // api.getConversations().then(setConversations);
  }, []);

  // TODO: Charger les messages de la conversation sélectionnée (API)
  useEffect(() => {
    if (selectedConv) {
      // api.getMessages(selectedConv.id).then(setMessages);
    }
  }, [selectedConv]);

  // TODO: WebSocket pour messages temps réel

  const handleSend = async () => {
    if (!input && !file) return;
    setLoading(true);
    try {
      let fileMeta = null;
      if (file) {
        // 1. Upload du fichier
        const formData = new FormData();
        formData.append("file", file);
        // TODO: ajouter le token
        const res = await api.post("/api/messages/upload", formData);
        fileMeta = res.data;
      }
      // 2. Envoi du message (texte ou fichier)
      const messagePayload = {
        receiverId: selectedConv?.userId || null,
        groupId: selectedConv?.groupId || null,
        content: input,
        type: file ? "file" : "text",
        filePath: fileMeta ? fileMeta.filePath : null,
      };
      let messageRes;
      if (file) {
        messageRes = await api.post("/api/messages/send-file?fileMeta=" + encodeURIComponent(Object.values(fileMeta).join("|")), messagePayload);
      } else {
        messageRes = await api.post("/api/messages", messagePayload);
      }
      setMessages((msgs) => [...msgs, messageRes.data.data]);
      setInput("");
      setFile(null);
      setPreviewUrl(null);
    } catch (e) {
      // TODO: gestion des erreurs
    }
    setLoading(false);
  };

  const handleFileChange = (e) => {
    const f = e.target.files[0];
    setFile(f);
    if (f && f.type.startsWith("image/")) {
      setPreviewUrl(URL.createObjectURL(f));
    } else {
      setPreviewUrl(null);
    }
  };

  return (
    <div className="flex h-full">
      {/* Liste des conversations */}
      <aside className="w-72 bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 p-4 overflow-y-auto">
        <h2 className="text-xl font-bold mb-4 text-blue-600">Conversations</h2>
        <ul className="space-y-2">
          {conversations.map((conv) => (
            <li
              key={conv.id}
              className={`p-3 rounded-lg cursor-pointer transition-colors ${selectedConv?.id === conv.id ? "bg-blue-100 dark:bg-blue-800 text-blue-800 dark:text-blue-100" : "hover:bg-gray-100 dark:hover:bg-gray-800"}`}
              onClick={() => setSelectedConv(conv)}
            >
              <div className="font-semibold">{conv.name || conv.username}</div>
              <div className="text-xs text-gray-500 dark:text-gray-400 truncate">{conv.lastMessage}</div>
            </li>
          ))}
        </ul>
      </aside>
      {/* Zone de messages */}
      <section className="flex-1 flex flex-col h-full">
        <div className="flex-1 overflow-y-auto p-6 bg-gray-50 dark:bg-gray-900">
          {messages.length === 0 ? (
            <div className="text-center text-gray-400 mt-20">Aucune conversation sélectionnée</div>
          ) : (
            <ul className="space-y-4">
              {messages.slice().reverse().map((msg) => (
                <li key={msg.id} className={`flex ${msg.senderId === 1 ? "justify-end" : "justify-start"}`}> {/* TODO: remplacer 1 par l'ID utilisateur courant */}
                  <div className={`max-w-lg px-4 py-2 rounded-2xl shadow-md ${msg.senderId === 1 ? "bg-blue-600 text-white" : "bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100"}`}>
                    {msg.type === "file" && msg.mimeType && msg.mimeType.startsWith("image/") && msg.thumbnailPath ? (
                      <img src={`/api/messages/thumbnail/${msg.id}`} alt="miniature" className="w-32 h-32 object-cover rounded mb-2" />
                    ) : null}
                    {msg.type === "file" && msg.mimeType && msg.mimeType.startsWith("video/") ? (
                      <video src={`/api/messages/preview/${msg.id}`} controls className="w-64 rounded mb-2" />
                    ) : null}
                    {msg.type === "file" && msg.mimeType && msg.mimeType.startsWith("audio/") ? (
                      <audio src={`/api/messages/preview/${msg.id}`} controls className="w-full mb-2" />
                    ) : null}
                    {msg.type === "file" && !msg.mimeType?.startsWith("image/") && !msg.mimeType?.startsWith("video/") && !msg.mimeType?.startsWith("audio/") ? (
                      <a href={`/api/messages/download/${msg.id}`} className="flex items-center gap-2 text-blue-500 underline mb-2" download>
                        <FiFile /> Télécharger le fichier
                      </a>
                    ) : null}
                    <div>{msg.content}</div>
                    <div className="flex items-center gap-2 mt-1 text-xs text-gray-400">
                      {msg.delivered && <FiCheck />}
                      {msg.read && (
                        <span className="inline-flex align-middle" title="Lu">
                          <FiCheck style={{ marginRight: -8 }} />
                          <FiCheck />
                        </span>
                      )}
                      {msg.editedAt && <FiEdit2 title="Édité" />}
                      {msg.deleted && <FiTrash2 title="Supprimé" />}
                      <span>{new Date(msg.sentAt).toLocaleTimeString()}</span>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
        {/* Zone de saisie */}
        <div className="p-4 border-t border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950 flex items-center gap-2">
          {previewUrl && (
            <img src={previewUrl} alt="aperçu" className="w-16 h-16 object-cover rounded mr-2" />
          )}
          <input
            type="text"
            className="flex-1 py-2 px-4 rounded-lg bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Écrire un message..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            disabled={loading}
          />
          <input
            type="file"
            ref={fileInputRef}
            className="hidden"
            onChange={handleFileChange}
            disabled={loading}
          />
          <button
            className="p-2 rounded-full bg-blue-100 dark:bg-blue-800 text-blue-600 dark:text-blue-200 hover:bg-blue-200 dark:hover:bg-blue-700 transition-colors"
            onClick={() => fileInputRef.current.click()}
            disabled={loading}
            title="Joindre un fichier"
          >
            <FiPaperclip />
          </button>
          <button
            className="p-2 rounded-full bg-blue-600 text-white hover:bg-blue-700 transition-colors ml-2"
            onClick={handleSend}
            disabled={loading || (!input && !file)}
            title="Envoyer"
          >
            <FiSend />
          </button>
        </div>
      </section>
    </div>
  );
} 