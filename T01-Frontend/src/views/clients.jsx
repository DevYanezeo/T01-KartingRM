import React, { useState, useEffect } from 'react';
import { FaUser, FaBirthdayCake, FaSearch, FaSync, FaExclamationTriangle } from 'react-icons/fa';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import './clients.css';

const Clients = () => {
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [today] = useState(new Date());

  // Función robusta para cargar clientes
  const fetchClients = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8090/api/client/all', {
        transformResponse: [
          (data) => {
            try {
              // Intenta parsear manualmente si falla el parseo automático
              return typeof data === 'string' ? JSON.parse(data) : data;
            } catch (e) {
              console.error("Error parsing JSON:", e);
              throw new Error("La respuesta del servidor no es válida");
            }
          }
        ]
      });
  
      if (!response.data) throw new Error("No se recibieron datos");
  
      // Validación adicional
      const clientsData = Array.isArray(response.data) ? response.data : [];
      
      setClients(clientsData.map(client => ({
        id: client.id,
        name: client.name || 'Sin nombre',
        email: client.email || 'Sin email',
        monthlyVisits: Number(client.monthlyVisits) || 0,
        birthDate: client.birthDate || ''
      })));
  
    } catch (err) {
      console.error("Error completo:", err);
      setError(
        err.response?.data?.message || 
        err.message || 
        "Error al cargar clientes. Verifica la consola para más detalles."
      );
      setClients([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClients();
  }, []);

  // Función segura para verificar cumpleaños
  const isBirthdayToday = (birthDate) => {
    try {
      if (!birthDate) return false;
      const date = new Date(birthDate);
      return date.getDate() === today.getDate() && 
             date.getMonth() === today.getMonth();
    } catch {
      return false;
    }
  };

  // Función segura para categoría
  const getClientCategory = (visits) => {
    const visitCount = Number(visits) || 0;
    if (visitCount >= 7) return 'Muy Frecuente';
    if (visitCount >= 5) return 'Frecuente';
    if (visitCount >= 2) return 'Regular';
    return 'No Frecuente';
  };

  // Filtrado seguro con búsqueda
  const filteredClients = clients.filter(client => {
    try {
      if (!client) return false;
      const name = client.name?.toString().toLowerCase() || '';
      const email = client.email?.toString().toLowerCase() || '';
      const term = searchTerm.toLowerCase();
      return name.includes(term) || email.includes(term);
    } catch {
      return false;
    }
  });

  // Formateo seguro de fecha
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleDateString('es-CL');
    } catch {
      return 'Fecha inválida';
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <Navbar />
        <div className="loading-state">
          <FaSync className="spinner" />
          <p>Cargando información de clientes...</p>
        </div>
        <Footer />
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <Navbar />
        <div className="error-state">
          <FaExclamationTriangle className="error-icon" />
          <h3>Error al cargar clientes</h3>
          <p>{error}</p>
          <button 
            onClick={fetchClients} 
            className="retry-btn"
            disabled={loading}
          >
            {loading ? 'Cargando...' : 'Reintentar'}
          </button>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="page-container">
      <Navbar />
      
      <main className="clients-page">
        <header className="clients-header">
          <h1>
            <FaUser className="header-icon" /> 
            Gestión de Clientes
            <span className="client-count">{clients.length} clientes registrados</span>
          </h1>
          
          <div className="controls">
            <div className="search-bar">
              <FaSearch className="search-icon" />
              <input 
                type="text" 
                placeholder="Buscar por nombre o email..." 
                className="search-input"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                disabled={loading}
              />
            </div>
            
            <button 
              className="refresh-btn"
              onClick={fetchClients}
              disabled={loading}
            >
              <FaSync className={loading ? 'spinning' : ''} /> 
              {loading ? 'Actualizando...' : 'Actualizar'}
            </button>
          </div>
        </header>

        <div className="clients-card">
          {filteredClients.length === 0 ? (
            <div className="empty-state">
              {searchTerm ? (
                <p>No se encontraron clientes que coincidan con "{searchTerm}"</p>
              ) : clients.length === 0 ? (
                <p>No hay clientes registrados en el sistema</p>
              ) : (
                <p>No hay clientes que coincidan con los filtros aplicados</p>
              )}
            </div>
          ) : (
            <>
              <div className="table-info">
                Mostrando {filteredClients.length} de {clients.length} clientes
              </div>
              
              <div className="table-responsive">
                <table className="clients-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Correo Electrónico</th>
                      <th>Visitas</th>
                      <th>Categoría</th>
                      <th>Cumpleaños</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredClients.map(client => (
                      <tr key={client.id}>
                        <td className="client-name">{client.name}</td>
                        <td className="client-email">{client.email}</td>
                        <td className="client-visits">{client.monthlyVisits}</td>
                        <td>
                          <span className={`client-category ${
                            getClientCategory(client.monthlyVisits).toLowerCase().replace(' ', '-')
                          }`}>
                            {getClientCategory(client.monthlyVisits)}
                          </span>
                        </td>
                        <td className="client-birthday">
                          {formatDate(client.birthDate)}
                          {isBirthdayToday(client.birthDate) && (
                            <span className="birthday-badge">
                              <FaBirthdayCake /> ¡Hoy!
                            </span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </div>
      </main>
      
      <Footer />
    </div>
  );
};

export default Clients;