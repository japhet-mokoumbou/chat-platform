import { BrowserRouter as Router, Routes, Route, Navigate, useLocation, useNavigate } from "react-router-dom";
import Sidebar from "./layout/Sidebar";
import Header from "./layout/Header";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Conversations from "./pages/Conversations";
import PrivateRoute from "./components/PrivateRoute";
import { useEffect, useState, createContext, useContext } from "react";
import Contacts from "./pages/Contacts";
import Groups from "./pages/Groups";
import Profile from "./components/Profile";
import Settings from "./components/Settings";


// Contexte pour l'état du sidebar
export const SidebarContext = createContext();

function Layout({ children }) {
  const { sidebarOpen } = useContext(SidebarContext);
  return (
    <div className={`min-h-screen flex bg-gradient-to-br from-blue-50 to-blue-100 dark:from-gray-900 dark:to-gray-800 transition-all duration-300 ${sidebarOpen ? 'pl-72' : 'pl-20'}`}>
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
  const [sidebarOpen, setSidebarOpen] = useState(true);

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
    <SidebarContext.Provider value={{ sidebarOpen, setSidebarOpen }}>
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
    </SidebarContext.Provider>
  );
}

// Dashboard corrigé avec un meilleur logo WhatsApp
function Dashboard() {
  return (
    <div className="h-full flex flex-col items-center justify-center bg-[#e5ddd5] rounded-xl shadow-lg">
      <div className="mb-4">
        <div className="bg-[#25d366] rounded-full p-4 shadow-lg flex items-center justify-center w-20 h-20">
          {/* Icône de chat simple et claire */}
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            {/* Bulle de chat principale */}
            <path 
              d="M36 20c0-8.84-7.16-16-16-16S4 11.16 4 20c0 3.2 0.94 6.18 2.56 8.68L4 36l7.68-2.52C14.02 35.1 16.86 36 20 36c8.84 0 16-7.16 16-16z" 
              fill="white"
            />
            {/* Points de conversation */}
            <circle cx="16" cy="20" r="2" fill="#25d366"/>
            <circle cx="24" cy="20" r="2" fill="#25d366"/>
            <circle cx="32" cy="20" r="2" fill="#25d366"/>
            {/* Petite queue de bulle */}
            <path 
              d="M20 36c-0.5 0-1 0-1.5-0.1L11 38l2.1-7.5c-1.2-1.8-1.9-4-1.9-6.5 0-6.6 5.4-12 12-12" 
              fill="white"
            />
          </svg>
        </div>
      </div>
      <h1 className="text-4xl font-extrabold text-[#075e54] drop-shadow-lg">
        Bienvenue sur <span className="text-[#25d366]">ChatESP</span>
      </h1>
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