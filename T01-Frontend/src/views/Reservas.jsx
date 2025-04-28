import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import { FaUser, FaUserPlus, FaCalendarAlt, FaClock, FaUsers, FaArrowRight, FaCheck } from 'react-icons/fa';
import './Reservas.css';

const Reservas = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [clients, setClients] = useState([]);
  const [loadingClients, setLoadingClients] = useState(false);
  const [error, setError] = useState(null);
  
  // Form data
  const [formData, setFormData] = useState({
    date: '',
    startTime: '',
    laps: 10, // Valor por defecto
    owner: {
      name: '',
      email: '',
      birthDate: ''
    },
    participants: []
  });
  
  const [newParticipant, setNewParticipant] = useState({
    name: '',
    email: '',
    birthDate: ''
  });

  // Cargar clientes existentes
  useEffect(() => {
    const fetchClients = async () => {
      setLoadingClients(true);
      try {
        const response = await axios.get('http://localhost:8090/api/client/all');
        setClients(response.data);
      } catch (err) {
        setError('Error al cargar clientes existentes');
        console.error(err);
      } finally {
        setLoadingClients(false);
      }
    };
    
    fetchClients();
  }, []);

  // Manejar cambios en el formulario principal
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Manejar cambios en el dueño de la reserva
  const handleOwnerChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      owner: {
        ...prev.owner,
        [name]: value
      }
    }));
  };

  // Manejar cambios en los participantes
  const handleParticipantChange = (e) => {
    const { name, value } = e.target;
    setNewParticipant(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Agregar un nuevo participante
  const addParticipant = async () => {
    // Validar campos requeridos
    if (!newParticipant.name || !newParticipant.email) {
      setError('Nombre y email son requeridos para cada participante');
      return;
    }

    try {
      // Verificar si el cliente ya existe
      const existingClient = clients.find(c => c.email === newParticipant.email);
      
      let participantId;
      if (existingClient) {
        participantId = existingClient.id;
      } else {
        // Registrar nuevo cliente
        const response = await axios.post('http://localhost:8090/api/client/registerClient', {
          name: newParticipant.name,
          email: newParticipant.email,
          birthDate: newParticipant.birthDate,
          monthlyVisits: 0
        });
        participantId = response.data.id;
        setClients(prev => [...prev, response.data]);
      }

      // Agregar a la lista de participantes
      setFormData(prev => ({
        ...prev,
        participants: [...prev.participants, {
          id: participantId,
          name: newParticipant.name,
          email: newParticipant.email
        }]
      }));

      // Limpiar formulario de participante
      setNewParticipant({
        name: '',
        email: '',
        birthDate: ''
      });
      setError(null);
    } catch (err) {
      setError('Error al registrar participante');
      console.error(err);
    }
  };

  // Eliminar participante
  const removeParticipant = (index) => {
    setFormData(prev => ({
      ...prev,
      participants: prev.participants.filter((_, i) => i !== index)
    }));
  };

  // Registrar el dueño y avanzar al paso 2
  const registerOwnerAndContinue = async () => {
    if (!formData.owner.name || !formData.owner.email) {
      setError('Nombre y email son requeridos para el dueño de la reserva');
      return;
    }

    try {
      // Verificar si el cliente ya existe
      const existingClient = clients.find(c => c.email === formData.owner.email);
      
      if (existingClient) {
        setFormData(prev => ({
          ...prev,
          owner: {
            ...prev.owner,
            id: existingClient.id
          }
        }));
      } else {
        // Registrar nuevo cliente
        const response = await axios.post('http://localhost:8090/api/client/registerClient', {
          name: formData.owner.name,
          email: formData.owner.email,
          birthDate: formData.owner.birthDate,
          monthlyVisits: 0
        });
        
        setFormData(prev => ({
          ...prev,
          owner: {
            ...prev.owner,
            id: response.data.id
          }
        }));
        
        setClients(prev => [...prev, response.data]);
      }

      setStep(2);
      setError(null);
    } catch (err) {
      setError('Error al registrar el dueño de la reserva');
      console.error(err);
    }
  };

  // Enviar reserva completa
  const submitReservation = async () => {
    if (!formData.date || !formData.startTime) {
      setError('Fecha y hora son requeridos');
      return;
    }

    try {
      const bookingRequest = {
        ownerId: formData.owner.id,
        participantIds: formData.participants.map(p => p.id),
        laps: formData.laps,
        date: formData.date,
        startTime: formData.startTime
      };

      await axios.post('http://localhost:8090/api/bookings', bookingRequest);
      
      // Redirigir a página de éxito o al calendario
      navigate('/calendar');
    } catch (err) {
      setError('Error al crear la reserva');
      console.error(err);
    }
  };

  return (
    <div className="reservas-page">
      <Navbar />
      
      <main className="reservas-container">
        <header className="reservas-header">
          <h1>Nueva Reserva</h1>
          <p>Complete los datos para crear una nueva reserva</p>
        </header>

        {/* Indicador de pasos */}
        <div className="steps-indicator">
          <div className={`step ${step >= 1 ? 'active' : ''}`}>
            <span>1</span>
            <p>Información del Cliente</p>
          </div>
          <div className={`step ${step >= 2 ? 'active' : ''}`}>
            <span>2</span>
            <p>Detalles de la Reserva</p>
          </div>
          <div className={`step ${step >= 3 ? 'active' : ''}`}>
            <span>3</span>
            <p>Confirmación</p>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {/* Paso 1: Información del cliente */}
        {step === 1 && (
          <div className="reservation-step">
            <h2><FaUser /> Información del Cliente Principal</h2>
            
            <div className="form-group">
              <label>Nombre completo</label>
              <input 
                type="text" 
                name="name" 
                value={formData.owner.name}
                onChange={handleOwnerChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>Email</label>
              <input 
                type="email" 
                name="email" 
                value={formData.owner.email}
                onChange={handleOwnerChange}
                required
              />
            </div>
            
            <div className="form-group">
              <label>Fecha de nacimiento</label>
              <input 
                type="date" 
                name="birthDate" 
                value={formData.owner.birthDate}
                onChange={handleOwnerChange}
              />
            </div>
            
            <button 
              className="btn-primary"
              onClick={registerOwnerAndContinue}
            >
              Siguiente <FaArrowRight />
            </button>
          </div>
        )}

        {/* Paso 2: Detalles de la reserva y participantes */}
        {step === 2 && (
          <div className="reservation-step">
            <h2><FaCalendarAlt /> Detalles de la Reserva</h2>
            
            <div className="form-row">
              <div className="form-group">
                <label>Fecha</label>
                <input 
                  type="date" 
                  name="date" 
                  value={formData.date}
                  onChange={handleInputChange}
                  required
                />
              </div>
              
              <div className="form-group">
                <label>Hora de inicio</label>
                <input 
                  type="time" 
                  name="startTime" 
                  value={formData.startTime}
                  onChange={handleInputChange}
                  required
                />
              </div>
              
              <div className="form-group">
                <label>Vueltas</label>
                <select 
                  name="laps" 
                  value={formData.laps}
                  onChange={handleInputChange}
                >
                  <option value="10">10 vueltas</option>
                  <option value="15">15 vueltas</option>
                  <option value="20">20 vueltas</option>
                  <option value="25">25 vueltas</option>
                </select>
              </div>
            </div>
            
            <h3><FaUsers /> Participantes adicionales</h3>
            
            <div className="participant-form">
              <div className="form-group">
                <label>Nombre</label>
                <input 
                  type="text" 
                  name="name" 
                  value={newParticipant.name}
                  onChange={handleParticipantChange}
                />
              </div>
              
              <div className="form-group">
                <label>Email</label>
                <input 
                  type="email" 
                  name="email" 
                  value={newParticipant.email}
                  onChange={handleParticipantChange}
                />
              </div>
              
              <div className="form-group">
                <label>Fecha nacimiento</label>
                <input 
                  type="date" 
                  name="birthDate" 
                  value={newParticipant.birthDate}
                  onChange={handleParticipantChange}
                />
              </div>
              
              <button 
                className="btn-secondary"
                onClick={addParticipant}
              >
                <FaUserPlus /> Agregar
              </button>
            </div>
            
            {formData.participants.length > 0 && (
              <div className="participants-list">
                <h4>Participantes agregados:</h4>
                <ul>
                  {formData.participants.map((p, index) => (
                    <li key={index}>
                      {p.name} ({p.email})
                      <button 
                        className="btn-remove"
                        onClick={() => removeParticipant(index)}
                      >
                        ×
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            )}
            
            <div className="step-actions">
              <button 
                className="btn-secondary"
                onClick={() => setStep(1)}
              >
                Volver
              </button>
              
              <button 
                className="btn-primary"
                onClick={() => setStep(3)}
              >
                Continuar <FaArrowRight />
              </button>
            </div>
          </div>
        )}

        {/* Paso 3: Confirmación */}
        {step === 3 && (
          <div className="reservation-step confirmation-step">
            <h2><FaCheck /> Confirmar Reserva</h2>
            
            <div className="confirmation-details">
              <h3>Resumen de la reserva</h3>
              
              <div className="detail-item">
                <strong>Cliente principal:</strong>
                <span>{formData.owner.name} ({formData.owner.email})</span>
              </div>
              
              <div className="detail-item">
                <strong>Fecha:</strong>
                <span>{formData.date}</span>
              </div>
              
              <div className="detail-item">
                <strong>Hora:</strong>
                <span>{formData.startTime}</span>
              </div>
              
              <div className="detail-item">
                <strong>Vueltas:</strong>
                <span>{formData.laps}</span>
              </div>
              
              {formData.participants.length > 0 && (
                <div className="detail-item">
                  <strong>Participantes:</strong>
                  <ul>
                    {formData.participants.map((p, index) => (
                      <li key={index}>{p.name} ({p.email})</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
            
            <div className="step-actions">
              <button 
                className="btn-secondary"
                onClick={() => setStep(2)}
              >
                Volver
              </button>
              
              <button 
                className="btn-primary"
                onClick={submitReservation}
              >
                Confirmar Reserva
              </button>
            </div>
          </div>
        )}
      </main>

      <Footer />
    </div>
  );
};

export default Reservas;