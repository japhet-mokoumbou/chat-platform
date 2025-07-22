import { useEffect, useState } from "react";

export default function Settings({ theme, setTheme }) {
  const [settings, setSettings] = useState({ notificationsEnabled: true, theme: theme || "light" });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    const fetchSettings = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await fetch("http://localhost:8080/api/user/settings", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (!res.ok) throw new Error("Erreur lors de la récupération des paramètres");
        const data = await res.json();
        setSettings({
          notificationsEnabled: data.notificationsEnabled ?? true,
          theme: data.theme ?? "light",
        });
        setTheme(data.theme ?? "light");
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };
    fetchSettings();
    // eslint-disable-next-line
  }, []);

  const handleChange = (e) => {
    const { name, type, checked, value } = e.target;
    const newValue = type === "checkbox" ? checked : value;
    setSettings((s) => ({
      ...s,
      [name]: newValue,
    }));
    if (name === "theme") {
      setTheme(newValue);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      const res = await fetch("http://localhost:8080/api/user/settings", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify(settings),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.error || "Erreur lors de la sauvegarde");
      setSuccess("Paramètres mis à jour !");
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="p-8">Chargement des paramètres...</div>;
  if (error) return <div className="p-8 text-red-600">{error}</div>;

  return (
    <div className="max-w-xl mx-auto p-8 bg-[#e5ddd5] rounded-xl shadow-lg mt-8">
      <h2 className="text-2xl font-bold text-[#075e54] mb-4">Paramètres</h2>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="flex items-center gap-2 text-lg text-[#075e54]">
            <input
              type="checkbox"
              name="notificationsEnabled"
              checked={settings.notificationsEnabled}
              onChange={handleChange}
              className="form-checkbox h-5 w-5 text-[#25d366]"
            />
            Activer les notifications
          </label>
        </div>
        <div>
          <label className="block text-lg mb-1 text-[#075e54]">Thème</label>
          <select
            name="theme"
            value={settings.theme}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 bg-[#f7f9fa] text-[#075e54] border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
          >
            <option value="light">Clair</option>
            <option value="dark">Sombre</option>
          </select>
        </div>
        <button
          type="submit"
          className="bg-[#25d366] text-white px-6 py-2 rounded hover:bg-[#20ba5a] disabled:opacity-50 shadow"
          disabled={saving}
        >
          {saving ? "Sauvegarde..." : "Enregistrer"}
        </button>
        {success && <div className="text-[#25d366] mt-2">{success}</div>}
        {error && <div className="text-red-600 mt-2">{error}</div>}
      </form>
    </div>
  );
} 