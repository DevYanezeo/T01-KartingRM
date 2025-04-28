import React, { useState, useEffect } from 'react';
import { FaFlagCheckered, FaTools, FaSearch, FaSync, FaCalendarAlt } from 'react-icons/fa';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import './Karts.css';

const Karts = () => {
  const [karts, setKarts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [maintenanceFilter, setMaintenanceFilter] = useState('all');

  const fetchKarts = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8090/api/kart');
      setKarts(response.data);
    } catch (err) {
      console.error('Error fetching karts:', err);
      setError(err.response?.data?.message || err.message || 'Error al cargar karts');
      setKarts([]);
    } finally {
      setLoading(false);
    }
  };

  const toggleMaintenance = async (kartCode, currentStatus) => {
    try {
      await axios.put(`http://localhost:8090/api/kart/${kartCode}/maintenance?underMaintenance=${!currentStatus}`);
      fetchKarts();
    } catch (err) {
      console.error('Error updating maintenance:', err);
      setError('Error al actualizar estado de mantenimiento');
    }
  };

  useEffect(() => {
    fetchKarts();
  }, []);

  const filteredKarts = karts.filter(kart => {
    const matchesSearch = 
      kart.kartCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
      kart.model.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesMaintenance = 
      maintenanceFilter === 'all' || 
      (maintenanceFilter === 'maintenance' && kart.underMaintenance) ||
      (maintenanceFilter === 'active' && !kart.underMaintenance);
    
    return matchesSearch && matchesMaintenance;
  });

  if (loading) {
    return (
      <div className="page-container">
        <Navbar />
        <div className="loading-state">
          <FaSync className="spinner" />
          <p>Cargando información de karts...</p>
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
          <h3>Error al cargar karts</h3>
          <p>{error}</p>
          <button onClick={fetchKarts} className="btn-primary">
            <FaSync /> Reintentar
          </button>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="page-container">
      <Navbar />
      
      <main className="karts-page">
        <header className="karts-header">
          <h1>
            <FaFlagCheckered className="header-icon" /> 
            Catálogo de Karts
            <span className="kart-count">{karts.length} unidades</span>
          </h1>
          
          <div className="controls">
            <div className="search-bar">
              <FaSearch className="search-icon" />
              <input 
                type="text" 
                placeholder="Buscar por código o modelo..." 
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            
            <div className="filter-group">
              <label>Estado:</label>
              <select 
                value={maintenanceFilter}
                onChange={(e) => setMaintenanceFilter(e.target.value)}
              >
                <option value="all">Todos</option>
                <option value="active">Disponibles</option>
                <option value="maintenance">En taller</option>
              </select>
            </div>
            
            <button onClick={fetchKarts} className="btn-secondary">
              <FaSync /> Actualizar
            </button>
          </div>
        </header>

        <div className="karts-container">
          {filteredKarts.length === 0 ? (
            <div className="empty-state">
              <p>No se encontraron karts con los filtros aplicados</p>
            </div>
          ) : (
            <div className="karts-grid">
              {filteredKarts.map(kart => (
                <div 
                  key={kart.kartCode} 
                  className={`kart-card ${kart.underMaintenance ? 'maintenance' : ''}`}
                >
                  <div className="kart-header">
                    <h3>{kart.kartCode}</h3>
                    <span className={`status-badge ${kart.underMaintenance ? 'warning' : 'success'}`}>
                      {kart.underMaintenance ? 'EN TALLER' : 'DISPONIBLE'}
                    </span>
                  </div>
                  
                  <div className="kart-details">
                    <p><strong>Modelo:</strong> {kart.model}</p>
                    <p><FaCalendarAlt /> <strong>Última revisión:</strong> {kart.lastMaintenance || 'No registrada'}</p>
                    {kart.description && (
                      <p className="kart-description">{kart.description}</p>
                    )}
                  </div>
                  
                  <div className="kart-actions">
                    <button 
                      onClick={() => toggleMaintenance(kart.kartCode, kart.underMaintenance)}
                      className={`status-toggle ${kart.underMaintenance ? 'end-maintenance' : 'start-maintenance'}`}
                    >
                      <FaTools /> {kart.underMaintenance ? 'Reparado' : 'Enviar a taller'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
      
      <Footer />
    </div>
  );
};

export default Karts;