import React from 'react';
import CalendarGrid from './CalendarGrid';
import './calendar.css'; 

const WeeklyCalendar = ({ data, onWeekChange, onCellClick }) => {
  // Función para formatear fechas de manera más amigable
  const formatDateRange = (startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    const startMonth = start.toLocaleString('es-ES', { month: 'long' });
    const endMonth = end.toLocaleString('es-ES', { month: 'long' });
    
    const startDay = start.getDate();
    const endDay = end.getDate();
    
    // Si es el mismo mes
    if (startMonth === endMonth) {
      return `${startDay} al ${endDay} de ${startMonth}`;
    }
    
    // Si son meses diferentes
    return `${startDay} de ${startMonth} al ${endDay} de ${endMonth}`;
  };
  
  // Obtener el mes que abarca la semana (o meses si cambia)
  const getMonthTitle = (startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    const startMonth = start.toLocaleString('es-ES', { month: 'long' });
    const endMonth = end.toLocaleString('es-ES', { month: 'long' });
    const year = start.getFullYear();
    
    if (startMonth === endMonth) {
      return `${startMonth.charAt(0).toUpperCase() + startMonth.slice(1)} ${year}`;
    }
    
    return `${startMonth.charAt(0).toUpperCase() + startMonth.slice(1)} - ${endMonth.charAt(0).toUpperCase() + endMonth.slice(1)} ${year}`;
  };
  
  // Verificar si la fecha actual está en esta semana
  const isCurrentWeek = () => {
    const today = new Date();
    const start = new Date(data.startDate);
    const end = new Date(data.endDate);
    
    return today >= start && today <= end;
  };

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden">
      <div className="calendar-header">
        <div className="current-month-label">
          {getMonthTitle(data.startDate, data.endDate)}
          {isCurrentWeek() && <span className="ml-2 px-2 py-1 bg-green-500 text-xs rounded-full text-white">Semana Actual</span>}
        </div>
        
        <div className="calendar-nav">
          <button 
            onClick={() => onWeekChange(-1)}
            className="nav-button"
          >
            ◄ Semana Anterior
          </button>
          
          <span className="date-display">
            {formatDateRange(data.startDate, data.endDate)}
          </span>
          
          <button 
            onClick={() => onWeekChange(1)}
            className="nav-button"
          >
            Siguiente Semana ►
          </button>
        </div>
      </div>
      
      <div className="overflow-x-auto">
        <CalendarGrid 
          data={data} 
          onCellClick={onCellClick}
        />
      </div>
    </div>
  );
};

export default WeeklyCalendar;