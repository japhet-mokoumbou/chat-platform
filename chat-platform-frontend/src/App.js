import { BrowserRouter as Router, Routes, Route, Navigate, useLocation, useNavigate } from "react-router-dom";
import Sidebar from "./layout/Sidebar";
import Header from "./layout/Header";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Conversations from "./pages/Conversations";
import PrivateRoute from "./components/PrivateRoute";
import { useEffect, useState } from "react";
import Contacts from "./pages/Contacts";
import Groups from "./pages/Groups";
import Profile from "./components/Profile";
import Settings from "./components/Settings";

function Layout({ children }) {
  return (
    <div className="min-h-screen flex bg-gradient-to-br from-blue-50 to-blue-100 dark:from-gray-900 dark:to-gray-800">
      <Sidebar />
      <div className="flex-1 flex flex-col">
        <Header />
        <main className="flex-1 p-6 overflow-y-auto">{children}</main>
      </div>
    </div>
  );
}

function App() {
  const location = useLocation();
  const navigate = useNavigate();
  const [isAuth, setIsAuth] = useState(!!localStorage.getItem("token"));
  const [theme, setTheme] = useState("light");

  // Appliquer le thème globalement
  useEffect(() => {
    if (theme === "dark") {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
  }, [theme]);

  // Charger le thème utilisateur au chargement si authentifié
  useEffect(() => {
    const fetchTheme = async () => {
      if (!localStorage.getItem("token")) return;
      try {
        const res = await fetch("http://localhost:8080/api/user/settings", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (!res.ok) return;
        const data = await res.json();
        setTheme(data.theme || "light");
      } catch (e) {
        setTheme("light");
      }
    };
    fetchTheme();
  }, [isAuth]);

  useEffect(() => {
    setIsAuth(!!localStorage.getItem("token"));
  }, [location]);

  // Vérification du token au chargement
  useEffect(() => {
    const checkToken = async () => {
      const token = localStorage.getItem("token");
      if (token) {
        try {
          const res = await fetch("http://localhost:8080/api/test/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (res.status === 401 || res.status === 403) {
            localStorage.removeItem("token");
            setIsAuth(false);
            navigate("/login", { replace: true });
          }
        } catch (e) {
          localStorage.removeItem("token");
          setIsAuth(false);
          navigate("/login", { replace: true });
        }
      }
    };
    checkToken();
    // eslint-disable-next-line
  }, [location]);

  // Affiche Login/Register sans layout si non authentifié
  if (!isAuth && (location.pathname === "/login" || location.pathname === "/register")) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    );
  }

  // Layout principal pour les pages protégées
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/conversations" element={<PrivateRoute><Conversations /></PrivateRoute>} />
        <Route path="/contacts" element={<PrivateRoute><Contacts /></PrivateRoute>} />
        <Route path="/groups" element={<PrivateRoute><Groups /></PrivateRoute>} />
        <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
        <Route path="/settings" element={<PrivateRoute><Settings theme={theme} setTheme={setTheme} /></PrivateRoute>} />
        {/* Les autres routes principales (contacts, groupes, etc.) */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Layout>
  );
}

// Dashboard temporaire pour la page d'accueil protégée
function Dashboard() {
  return (
    <div className="h-full flex items-center justify-center">
      <h1 className="text-4xl font-extrabold text-blue-700 dark:text-blue-300 drop-shadow-lg">Bienvenue sur <span className="text-blue-500">ChatESP</span></h1>
    </div>
  );
}

export default function AppWithRouter() {
  return (
    <Router>
      <App />
    </Router>
  );
} 