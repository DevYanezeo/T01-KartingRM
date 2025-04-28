import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import './home.css';

const Home = () => {
  const [stats, setStats] = useState([
    { title: "Clientes Registrados", value: "0", trend: "up" },
  ]);

  const [recentReservations, setRecentReservations] = useState([]);
  const [loadingReservations, setLoadingReservations] = useState(true);

  // Función para obtener clientes del backend
  const fetchClientCount = async () => {
    try {
      const response = await axios.get('http://localhost:8090/api/client/all');
      const clientsData = Array.isArray(response.data) ? response.data : [];
      
      setStats(prevStats => prevStats.map(stat => 
        stat.title === "Clientes Registrados" 
          ? { ...stat, value: clientsData.length.toString() } 
          : stat
      ));
    } catch (err) {
      console.error("Error al obtener clientes:", err);
    }
  };

  // Función para obtener reservas del día actual
  const fetchTodayReservations = async () => {
    try {
      setLoadingReservations(true);
      const today = new Date().toISOString().split('T')[0]; // Formato YYYY-MM-DD
      const response = await axios.get(`http://localhost:8090/api/bookings/by-date?date=${today}`);
      
      // Mapeamos los datos del backend al formato que necesita el frontend
      const formattedReservations = response.data.map(reservation => ({
        id: reservation.reservationCode,
        client: reservation.ownerName,
        kart: reservation.assignedKarts.join(', '),
        time: `${reservation.startTime.substring(0, 5)} - ${reservation.endTime.substring(0, 5)}`,
        package: `${reservation.laps} vueltas`
      }));
      
      setRecentReservations(formattedReservations);
    } catch (err) {
      console.error("Error al obtener reservas:", err);
    } finally {
      setLoadingReservations(false);
    }
  };

  useEffect(() => {
    fetchClientCount();
    fetchTodayReservations();
  }, []);

  return (
    <div className="admin-home">
      <Navbar />
      
      <main className="dashboard-container">
        <header className="dashboard-header">
          <h1>Panel de Control</h1>
          <p>Bienvenido al sistema de gestión de KartingRM</p>
        </header>

                {/* Acciones rápidas */}
        <section className="quick-actions">
          <h2>Acciones Rápidas</h2>
          <div className="actions-grid">
            <Link to="/reservas" className="action-card">
              <div className="action-icon">➕</div>
              <span>Nueva Reserva</span>
            </Link>
            <Link to="/reportes" className="action-card">
              <div className="action-icon">📊</div>
              <span>Visualizar Reportes</span>
            </Link>
            <Link to="/karts" className="action-card">
              <div className="action-icon">🏎️</div>
              <span>Gestión de Karts</span>
            </Link>
            <Link to="/tarifas" className="action-card">
              <div className="action-icon">⚙️</div>
              <span>Configurar Tarifas</span>
            </Link>
          </div>
        </section>
        
        {/* Estadísticas rápidas */}
        <section className="stats-grid">
          {stats.map((stat, index) => (
            <div key={index} className={`stat-card ${stat.trend}`}>
              <h3>{stat.title}</h3>
              <div className="stat-value">{stat.value}</div>
              <div className="stat-change">
                {stat.change} {stat.trend === 'up' ? '↑' : '↓'}
              </div>
            </div>
          ))}
        </section>

        
        {/* Reservas recientes */}
        <section className="recent-section">
          <div className="section-header">
            <h2>Reservas De Hoy</h2>
            <Link to="/calendar" className="view-all">Ver todas →</Link>
          </div>
          <div className="reservations-table">
            <div className="table-header">
              <span>ID Reserva</span>
              <span>Cliente</span>
              <span>Kart(s)</span>
              <span>Horario</span>
              <span>Paquete</span>
            </div>
            {loadingReservations ? (
              <div className="loading-message">Cargando reservas...</div>
            ) : recentReservations.length > 0 ? (
              recentReservations.map((res, index) => (
                <div key={index} className="table-row">
                  <span className="reservation-id">{res.id}</span>
                  <span>{res.client}</span>
                  <span>{res.kart}</span>
                  <span>{res.time}</span>
                  <span>{res.package}</span>
                </div>
              ))
            ) : (
              <div className="no-reservations">No hay reservas para hoy</div>
            )}
          </div>
        </section>
        

      </main>

      <Footer />
    </div>
  );
};

export default Home;