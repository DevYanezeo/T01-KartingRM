import React, { useState } from 'react';
import axios from 'axios';
import './CreateBookingButton';

const CreateBookingButton = ({ onBookingCreated }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    clientName: '',
    date: '',
    startTime: '09:00',
    duration: 30,
    karts: 1,
    pricingOption: 'standard'
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8090/api',
    headers: {
      'Content-Type': 'application/json'
    }
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    setSuccess(null);

    try {
      // Validar datos antes de enviar
      if (!formData.clientName || !formData.date) {
        throw new Error('Nombre del cliente y fecha son requeridos');
      }

      // Preparar los datos para el backend
      const bookingRequest = {
        clientName: formData.clientName,
        date: formData.date,
        startTime: formData.startTime,
        duration: parseInt(formData.duration),
        karts: parseInt(formData.karts),
        pricingOption: formData.pricingOption
      };

      // Enviar al backend
      const response = await api.post('/bookings', bookingRequest);
      
      // Notificar éxito
      setSuccess('Reserva creada exitosamente!');
      
      // Resetear formulario
      setFormData({
        clientName: '',
        date: '',
        startTime: '09:00',
        duration: 30,
        karts: 1,
        pricingOption: 'standard'
      });

      // Cerrar modal después de 2 segundos
      setTimeout(() => {
        setIsModalOpen(false);
        if (onBookingCreated) {
          onBookingCreated();
        }
      }, 2000);

    } catch (err) {
      console.error('Error al crear reserva:', err);
      setError(err.response?.data?.message || err.message || 'Error al crear la reserva');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <section className="booking-button-section">
      <div className="container mx-auto px-4 py-4">
        <button 
          onClick={() => setIsModalOpen(true)}
          className="create-booking-btn"
        >
          Crear Nueva Reserva
        </button>
      </div>

      {/* Modal para crear reserva */}
      {isModalOpen && (
        <div className="booking-modal-overlay">
          <div className="booking-modal">
            <div className="modal-header">
              <h3>Crear Nueva Reserva</h3>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="modal-close-btn"
                disabled={isSubmitting}
              >
                &times;
              </button>
            </div>

            <form onSubmit={handleSubmit} className="booking-form">
              {error && (
                <div className="form-error">
                  {error}
                </div>
              )}

              {success && (
                <div className="form-success">
                  {success}
                </div>
              )}

              <div className="form-group">
                <label htmlFor="clientName">Nombre del Cliente</label>
                <input
                  type="text"
                  id="clientName"
                  name="clientName"
                  value={formData.clientName}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="date">Fecha</label>
                <input
                  type="date"
                  id="date"
                  name="date"
                  value={formData.date}
                  onChange={handleInputChange}
                  required
                  min={new Date().toISOString().split('T')[0]}
                />
              </div>

              <div className="form-group">
                <label htmlFor="startTime">Hora de Inicio</label>
                <select
                  id="startTime"
                  name="startTime"
                  value={formData.startTime}
                  onChange={handleInputChange}
                  required
                >
                  {Array.from({ length: 14 }, (_, i) => {
                    const hour = 9 + Math.floor(i / 2);
                    const minute = (i % 2) * 30;
                    const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                    return (
                      <option key={time} value={time}>
                        {time}
                      </option>
                    );
                  })}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="duration">Duración (minutos)</label>
                <select
                  id="duration"
                  name="duration"
                  value={formData.duration}
                  onChange={handleInputChange}
                  required
                >
                  <option value="30">30 minutos</option>
                  <option value="60">60 minutos</option>
                  <option value="90">90 minutos</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="karts">Número de Karts</label>
                <select
                  id="karts"
                  name="karts"
                  value={formData.karts}
                  onChange={handleInputChange}
                  required
                >
                  {[1, 2, 3, 4, 5, 6].map(num => (
                    <option key={num} value={num}>{num}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="pricingOption">Opción de Precio</label>
                <select
                  id="pricingOption"
                  name="pricingOption"
                  value={formData.pricingOption}
                  onChange={handleInputChange}
                  required
                >
                  <option value="standard">Standard</option>
                  <option value="premium">Premium</option>
                  <option value="group">Grupo</option>
                </select>
              </div>

              <div className="form-actions">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="cancel-btn"
                  disabled={isSubmitting}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="submit-btn"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Creando...' : 'Crear Reserva'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
};

export default CreateBookingButton;