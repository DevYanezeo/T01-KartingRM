import React from 'react';
import CalendarCell from './CalendarCell';

const CalendarGrid = ({ data, onCellClick }) => {
  if (!data || !data.timeSlots || !data.daysHeader || !data.calendarGrid) {
    return (
      <div className="p-4 bg-yellow-50 border-l-4 border-yellow-400 text-yellow-700">
        <p>No se encontraron datos para mostrar el calendario.</p>
        <p>Por favor, verifica la conexión con el servidor.</p>
      </div>
    );
  }
 
  const today = new Date();
  const formattedToday = today.toISOString().split('T')[0];
 
  // Función para determinar si una fecha es fin de semana
  const isWeekend = (dayIndex) => {
    return dayIndex === 0 || dayIndex === 6; // Domingo o sábado
  };
 
  // Función para determinar si una fecha es la actual
  const isToday = (dateStr) => {
    return dateStr === formattedToday;
  };
 
  // Función para calcular la duración en minutos
  const calculateDurationMinutes = (startTime, endTime) => {
    if (!startTime || !endTime) return 60; // Por defecto 1 hora
   
    const [startHours, startMinutes] = startTime.substring(0, 5).split(':').map(Number);
    const [endHours, endMinutes] = endTime.substring(0, 5).split(':').map(Number);
   
    const startTotalMinutes = startHours * 60 + startMinutes;
    const endTotalMinutes = endHours * 60 + endMinutes;
   
    return endTotalMinutes - startTotalMinutes;
  };
 
  // Función para calcular la altura en base a la duración (5 min incrementos)
  const calculateHeightClass = (durationMinutes) => {
    // Cada 5 minutos aumenta la altura
    const steps = Math.ceil(durationMinutes / 5);
    return `duration-${steps * 5}`; // Ejemplo: duration-15, duration-30, etc.
  };
 
  // Filtrar los timeSlots para mostrar solo intervalos de 1 hora (00:00)
  const hourlyTimeSlots = data.timeSlots.filter(slot => {
    return slot.substring(3, 5) === "00"; // Solo mostrar los slots XX:00
  });

  // Crear un mapa de todas las reservas
  const getAllBookings = () => {
    const allBookings = {};
    
    data.timeSlots.forEach(timeSlot => {
      const timeSlotKey = timeSlot.substring(0, 5);
      const timeSlotData = data.calendarGrid[timeSlotKey] || {};
      
      data.daysHeader.forEach((_, dayIndex) => {
        const date = new Date(data.startDate);
        date.setDate(date.getDate() + dayIndex);
        const dateKey = date.toISOString().split('T')[0];
        
        const cellData = timeSlotData[dateKey]?.[0];
        
        if (cellData && cellData.status === "BOOKED") {
          // Crear una clave única para esta reserva
          const bookingKey = `${cellData.bookingId || cellData.clientName}_${cellData.startTime}_${cellData.endTime}`;
          
          if (!allBookings[bookingKey]) {
            allBookings[bookingKey] = {
              ...cellData,
              dateKey,
              processed: false
            };
          }
        }
      });
    });
    
    return allBookings;
  };
  
  // Función para organizar todas las reservas por día y hora
  const organizeBookings = () => {
    const bookings = {};
    const allBookings = getAllBookings();
    
    // Inicializar la estructura de datos
    hourlyTimeSlots.forEach(hourSlot => {
      const hourKey = hourSlot.substring(0, 5);
      bookings[hourKey] = {};
      
      data.daysHeader.forEach((_, dayIndex) => {
        const date = new Date(data.startDate);
        date.setDate(date.getDate() + dayIndex);
        const dateKey = date.toISOString().split('T')[0];
        
        bookings[hourKey][dateKey] = [];
      });
    });
    
    // Procesar cada reserva una sola vez y colocarla en la hora correcta
    Object.values(allBookings).forEach(booking => {
      if (booking.processed) return;
      
      const startTimeStr = booking.startTime.substring(0, 5);
      const [startHour, startMinute] = startTimeStr.split(':').map(Number);
      
      // Determinar a qué hora pertenece
      const belongsToHour = `${String(startHour).padStart(2, '0')}:00`;
      
      // Calcular la posición relativa dentro de la hora
      const positionPercent = (startMinute / 60) * 100;
      
      // Calcular la duración
      const durationMinutes = calculateDurationMinutes(booking.startTime, booking.endTime);
      const heightClass = calculateHeightClass(durationMinutes);
      
      // Verificar si la hora existe en nuestra estructura
      if (bookings[belongsToHour] && bookings[belongsToHour][booking.dateKey]) {
        bookings[belongsToHour][booking.dateKey].push({
          ...booking,
          positionPercent,
          durationMinutes,
          heightClass,
          originalTimeSlot: startTimeStr
        });
      }
      
      // Marcar como procesada
      booking.processed = true;
    });
    
    return bookings;
  };
  
  const organizedBookings = organizeBookings();
  
  // Verificar si una celda está completamente disponible
  const isCellFullyAvailable = (hourSlot, dateKey) => {
    return !organizedBookings[hourSlot]?.[dateKey]?.length;
  };

  return (
    <div className="calendar-scroll-container">
      <table className="calendar-table">
        <thead>
          <tr>
            <th className="time-column">Hora</th>
            {data.daysHeader.map((dayHeader, index) => {
              const date = new Date(data.startDate);
              date.setDate(date.getDate() + index);
              const dateStr = date.toISOString().split('T')[0];
             
              // Extraer el nombre del día y el número
              const parts = dayHeader.split(' ');
              const dayName = parts[0];
              const dayNumber = parts[1];
             
              // Obtener el mes para mostrarlo
              const month = date.toLocaleString('es-ES', { month: 'short' });
              return (
                <th
                  key={index}
                  className={isWeekend(index) ? 'weekend-column' : ''}
                >
                  <div>{dayName}</div>
                  <div className={isToday(dateStr) ? 'today-indicator' : ''}>
                    {dayNumber} {month}
                  </div>
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody>
          {hourlyTimeSlots.map((timeSlot, timeIndex) => {
            const hourSlot = timeSlot.substring(0, 5);
           
            return (
              <tr key={timeIndex} className="hour-row">
                <td className="time-column">{hourSlot}</td>
                {data.daysHeader.map((_, dayIndex) => {
                  const date = new Date(data.startDate);
                  date.setDate(date.getDate() + dayIndex);
                  const dateKey = date.toISOString().split('T')[0];
                  
                  // Verificar si esta celda está disponible completamente
                  const isAvailable = isCellFullyAvailable(hourSlot, dateKey);
                  
                  // Obtener las reservas para esta hora y fecha
                  const cellBookings = organizedBookings[hourSlot]?.[dateKey] || [];
                  
                  // Calcular la siguiente hora para enviarla al evento onClick
                  const nextHour = addHour(hourSlot + ":00");
                  
                  return (
                    <td
                      key={`${timeIndex}-${dayIndex}`}
                      className={`hour-cell ${isWeekend(dayIndex) ? 'weekend-column' : ''}`}
                    >
                      {/* Contenedor para posicionamiento relativo */}
                      <div className="hour-cell-container">
                        {/* Celda disponible de fondo */}
                        <div
                          className="available-cell full-hour"
                          style={{ opacity: isAvailable ? 0.7 : 0.3 }}
                          onClick={() => onCellClick && onCellClick({
                            status: isAvailable ? "AVAILABLE" : "PARTIALLY_AVAILABLE",
                            startTime: hourSlot + ":00",
                            endTime: nextHour
                          }, dateKey, hourSlot)}
                        >
                          <span>{isAvailable ? "Disponible" : "Parcialmente Disponible"}</span>
                        </div>
                        
                        {/* Renderizar cada reserva con su posición y tamaño adecuados */}
                        {cellBookings.map((booking, idx) => {
                          // Determinar si la reserva excede la hora actual
                          const exceedsHour = booking.durationMinutes + 
                              parseInt(booking.originalTimeSlot.substring(3, 5)) > 60;
                              
                          // Calcular si la reserva es demasiado corta para mostrar todos los detalles
                          const isVeryShort = booking.durationMinutes < 15;
                          
                          return (
                            <div
                              key={idx}
                              className={`booking-position-wrapper ${booking.heightClass}`}
                              style={{
                                top: `${booking.positionPercent}%`
                              }}
                            >
                              <CalendarCell
                                data={booking}
                                onClick={() => onCellClick && onCellClick(booking, dateKey, booking.originalTimeSlot)}
                                proportionalHeight={true}
                                durationMinutes={booking.durationMinutes}
                                exceedsHour={exceedsHour}
                                isVeryShort={isVeryShort}
                              />
                            </div>
                          );
                        })}
                      </div>
                    </td>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

// Función auxiliar para sumar 1 hora
function addHour(timeStr) {
  const [hours, minutes, seconds] = timeStr.split(':');
  const hour = (parseInt(hours) + 1) % 24;
  return `${String(hour).padStart(2, '0')}:${minutes}:${seconds || '00'}`;
}

export default CalendarGrid;