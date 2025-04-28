import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import WeeklyCalendar from '../components/calendar/WeeklyCalendar';
import CreateBookingButton from './CreateBookingButton';
import '../components/calendar/calendar.css';

const CalendarView = () => {
  const [calendarData, setCalendarData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentWeek, setCurrentWeek] = useState(new Date());
  const [selectedCell, setSelectedCell] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Configuración de Axios con manejo de errores mejorado
  const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8090/api',
    headers: {
      'Content-Type': 'application/json'
    }
  });

  useEffect(() => {
    const fetchCalendarData = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Obtener la fecha de inicio de la semana (domingo)
        const startDate = getStartOfWeek(currentWeek);
        const formattedStartDate = formatDate(startDate);
        
        console.log("Fetching data for:", formattedStartDate);
        
        // Simular la respuesta si estamos en desarrollo o hacer la petición real
        let response;
        if (process.env.NODE_ENV === 'development' && import.meta.env.VITE_USE_MOCK_DATA === 'true') {
          // Usar datos de ejemplo (puedes crear un archivo mock)
          response = { data: getMockCalendarData(startDate) };
        } else {
          // Petición real al backend
          response = await api.get('/calendar/weekly', {
            params: { startDate: formattedStartDate }
          });
        }
        
        if (!response.data || !response.data.calendarGrid) {
          throw new Error("Datos del calendario no recibidos correctamente");
        }
        
        console.log("Datos recibidos:", response.data);
        setCalendarData(response.data);
      } catch (err) {
        console.error("Error completo:", {
          message: err.message,
          config: err.config,
          response: err.response?.data
        });
        setError(err.response?.data?.message || err.message || "Error al cargar el calendario");
      } finally {
        setLoading(false);
      }
    };

    fetchCalendarData();
  }, [currentWeek]);

  // Función para obtener el inicio de la semana (domingo)
  const getStartOfWeek = (date) => {
    const d = new Date(date);
    const day = d.getDay(); // 0 = domingo, 1 = lunes, etc.
    d.setDate(d.getDate() - day); // Restamos los días para llegar al domingo
    return d;
  };

  // Función para formatear la fecha en formato YYYY-MM-DD
  const formatDate = (date) => {
    return date.toISOString().split('T')[0];
  };

  const handleWeekChange = (weeksOffset) => {
    const newDate = new Date(currentWeek);
    newDate.setDate(newDate.getDate() + (weeksOffset * 7));
    setCurrentWeek(newDate);
  };

  const handleCellClick = (cellData, dateStr, timeStr) => {
    setSelectedCell({
      ...cellData,
      date: dateStr,
      formattedTime: timeStr
    });
    setIsModalOpen(true);
  };

  const handleSaveBooking = async (bookingData) => {
    try {
      // Aquí iría la lógica para guardar la reserva en el backend
      console.log("Guardando reserva:", bookingData);
      
      // Ejemplo:
      // await api.post('/bookings', bookingData);
      
      // Recargar los datos del calendario después de guardar
      const startDate = getStartOfWeek(currentWeek);
      const formattedStartDate = formatDate(startDate);
      const response = await api.get('/calendar/weekly', {
        params: { startDate: formattedStartDate }
      });
      
      setCalendarData(response.data);
      
      // Feedback al usuario
      alert("Reserva guardada con éxito");
    } catch (err) {
      console.error("Error al guardar la reserva:", err);
      alert("Error al guardar la reserva: " + (err.response?.data?.message || err.message));
    }
  };

  // Función para generar datos de ejemplo (solo para desarrollo)
  const getMockCalendarData = (startDate) => {
    // Crear una copia profunda de los datos de ejemplo para no modificar el original
    const mockData = JSON.parse(JSON.stringify({
      startDate: formatDate(startDate),
      endDate: formatDate(new Date(startDate.getTime() + 6 * 24 * 60 * 60 * 1000)),
      daysHeader: generateDaysHeader(startDate),
      timeSlots: generateTimeSlots(),
      calendarGrid: generateCalendarGrid(startDate)
    }));
    
    return mockData;
  };

  // Generar los encabezados de los días correctamente
  const generateDaysHeader = (startDate) => {
    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const headers = [];
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(startDate);
      date.setDate(date.getDate() + i);
      const dayName = days[date.getDay()];
      const dayNumber = date.getDate();
      headers.push(`${dayName} ${dayNumber}`);
    }
    
    return headers;
  };

  // Generar franjas horarias de ejemplo
  const generateTimeSlots = () => {
    const slots = [];
    for (let hour = 9; hour < 23; hour++) {
      slots.push(`${hour.toString().padStart(2, '0')}:00:00`);
      slots.push(`${hour.toString().padStart(2, '0')}:30:00`);
    }
    return slots;
  };

  // Generar grid del calendario
  const generateCalendarGrid = (startDate) => {
    const grid = {};
    const timeSlots = generateTimeSlots();
    
    // Para cada franja horaria
    timeSlots.forEach(slot => {
      const timeKey = slot.substring(0, 5);
      grid[timeKey] = {};
      
      // Para cada día de la semana
      for (let i = 0; i < 7; i++) {
        const date = new Date(startDate);
        date.setDate(date.getDate() + i);
        const dateKey = formatDate(date);
        
        // La mayoría serán disponibles
        grid[timeKey][dateKey] = [{
          bookingId: null,
          reservationCode: null,
          clientName: null,
          startTime: slot,
          endTime: addMinutesToTime(slot, 30),
          assignedKarts: null,
          status: "AVAILABLE",
          pricingInfo: null
        }];
        
      }
    });
    
    return grid;
  };

  const addMinutesToTime = (timeStr, minutes) => {
    const [hours, mins, secs] = timeStr.split(':').map(Number);
    const totalMinutes = hours * 60 + mins + minutes;
    const newHours = Math.floor(totalMinutes / 60) % 24;
    const newMins = totalMinutes % 60;
    return `${String(newHours).padStart(2, '0')}:${String(newMins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  };

  const refreshCalendarData = async () => {
    try {
      setLoading(true);
      const startDate = getStartOfWeek(currentWeek);
      const formattedStartDate = formatDate(startDate);
      const response = await api.get('/calendar/weekly', {
        params: { startDate: formattedStartDate }
      });
      setCalendarData(response.data);
    } catch (err) {
      console.error("Error al recargar el calendario:", err);
      setError(err.response?.data?.message || err.message || "Error al recargar el calendario");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col bg-gray-50">
        <Navbar />
        <div className="flex-grow container mx-auto px-4 py-8">
          <div className="bg-white rounded-lg shadow-lg p-6">
            <div className="animate-pulse space-y-6">
              <div className="h-10 bg-gray-200 rounded-md w-1/3 mx-auto"></div>
              <div className="flex justify-between">
                <div className="h-8 bg-gray-200 rounded-md w-28"></div>
                <div className="h-8 bg-gray-200 rounded-md w-40"></div>
                <div className="h-8 bg-gray-200 rounded-md w-28"></div>
              </div>
              <div className="h-80 bg-gray-200 rounded-md w-full"></div>
            </div>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex flex-col bg-gray-50">
        <Navbar />
        <div className="flex-grow container mx-auto px-4 py-8">
          <div className="bg-white rounded-lg shadow-lg p-6">
            <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4">
              <h3 className="font-bold">Error al cargar el calendario</h3>
              <p>{error}</p>
              <button
                onClick={() => window.location.reload()}
                className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              >
                Reintentar
              </button>
            </div>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      <Navbar />
      <CreateBookingButton onBookingCreated={refreshCalendarData} />
      <main className="flex-grow container mx-auto px-4 py-8">
        {calendarData && (
          <WeeklyCalendar 
            data={calendarData} 
            onWeekChange={handleWeekChange}
            onCellClick={handleCellClick}
          />
        )}
      </main>
      <Footer />
    </div>
  );
};

export default CalendarView;