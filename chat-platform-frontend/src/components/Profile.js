import { useEffect, useState, useRef } from "react";

// Fonction utilitaire pour l'URL de la photo de profil
const getProfilePictureUrl = (url) => {
  if (!url) return "";
  if (url.startsWith("/uploads/")) {
    return "http://localhost:8080" + url;
  }
  return url;
};

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ displayName: "", bio: "", profilePicture: "" });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef();

  useEffect(() => {
    const fetchProfile = async () => {
      setLoading(true);
      setError("");
      try {
        const res = await fetch("http://localhost:8080/api/user/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (!res.ok) throw new Error("Erreur lors de la récupération du profil");
        const data = await res.json();
        setProfile(data);
        setForm({
          displayName: data.displayName || "",
          bio: data.bio || "",
          profilePicture: data.profilePicture || "",
        });
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      const res = await fetch("http://localhost:8080/api/user/profile", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify(form),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.error || "Erreur lors de la sauvegarde");
      setSuccess("Profil mis à jour !");
      setProfile(data.user);
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setUploading(true);
    setError("");
    setSuccess("");
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await fetch("http://localhost:8080/api/user/profile-picture", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: formData,
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.error || "Erreur lors de l'upload");
      setForm((f) => ({ ...f, profilePicture: data.url }));
      setProfile((p) => ({ ...p, profilePicture: data.url }));
      setSuccess("Photo de profil mise à jour !");
    } catch (e) {
      setError(e.message);
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
    }
  };

  if (loading) return <div className="p-8">Chargement du profil...</div>;
  if (error) return <div className="p-8 text-red-600">{error}</div>;

  return (
    <div className="max-w-xl mx-auto p-8 bg-[#e5ddd5] rounded-xl shadow-lg mt-8">
      <h2 className="text-2xl font-bold text-[#075e54] mb-4">Mon profil</h2>
      <div className="mb-6">
        <div className="flex items-center gap-4 mb-2">
          {profile?.profilePicture ? (
            <img src={getProfilePictureUrl(profile.profilePicture)} alt="Avatar" className="w-20 h-20 rounded-full object-cover border-2 border-[#25d366]" />
          ) : (
            <div className="w-20 h-20 rounded-full bg-[#f7f9fa] flex items-center justify-center text-3xl text-[#25d366] border-2 border-[#25d366]">
              <span>{profile?.displayName?.[0] || profile?.username?.[0] || "?"}</span>
            </div>
          )}
          <div>
            <div className="font-semibold text-lg text-[#075e54]">{profile?.displayName || profile?.username}</div>
            <div className="text-gray-500 text-sm">{profile?.email}</div>
            <div className="text-gray-400 text-xs">@{profile?.username}</div>
            <div className="mt-2">
              <input
                type="file"
                accept="image/jpeg,image/png,image/webp"
                onChange={handleFileChange}
                ref={fileInputRef}
                className="block w-full text-sm text-[#075e54] file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-[#25d366] file:text-white hover:file:bg-[#20ba5a]"
                disabled={uploading}
              />
              {uploading && <span className="text-[#25d366] ml-2">Upload...</span>}
            </div>
          </div>
        </div>
      </div>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1 text-[#075e54]">Nom affiché</label>
          <input
            type="text"
            name="displayName"
            value={form.displayName}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 bg-[#f7f9fa] text-[#075e54] border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
            maxLength={50}
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1 text-[#075e54]">Bio</label>
          <textarea
            name="bio"
            value={form.bio}
            onChange={handleChange}
            className="w-full border rounded px-3 py-2 bg-[#f7f9fa] text-[#075e54] border-[#d1d7db] focus:outline-none focus:ring-2 focus:ring-[#25d366]"
            maxLength={255}
            rows={3}
          />
        </div>
        {/* Champ profilePicture masqué, géré par upload */}
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