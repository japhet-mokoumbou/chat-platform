import { useState, useEffect } from 'react';
import axios from 'axios';

function Home() {
  const [message, setMessage] = useState('');

  useEffect(() => {
    axios.get('http://localhost:8080/api/test')
      .then(response => setMessage(response.data.message))
      .catch(error => console.error('Error fetching test endpoint:', error));
  }, []);

  return (
    <div className="flex flex-col items-center justify-center h-screen">
      <h1 className="text-3xl font-bold">Bienvenue sur la plateforme de discussion</h1>
      <p className="mt-4">{message || 'Connexion au backend en cours...'}</p>
    </div>
  );
}

export default Home;