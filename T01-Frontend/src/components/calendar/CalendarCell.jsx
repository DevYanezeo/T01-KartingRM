import React from 'react';
import PropTypes from 'prop-types';

const CalendarCell = ({ data, onClick, proportionalHeight, durationMinutes, exceedsHour, isVeryShort }) => {
  // Verifica si la celda está disponible
  if (!data || data.status === "AVAILABLE" || data.status === "PARTIALLY_AVAILABLE") {
    return (
      <div className={`available-cell ${data.status === "PARTIALLY_AVAILABLE" ? "partially-available" : ""}`} onClick={onClick}>
        <span>{data.status === "PARTIALLY_AVAILABLE" ? "Parcialmente Disponible" : "Disponible"}</span>
      </div>
    );
  }
 
  // Determinar clase CSS en función de las propiedades
  const getCellClass = () => {
    const baseClass = "booked-cell";
    let additionalClasses = [];
    
    if (proportionalHeight) {
      additionalClasses.push("proportional-height");
    }
    
    if (exceedsHour) {
      additionalClasses.push("exceeds-hour");
    }
    
    if (isVeryShort) {
      additionalClasses.push("very-short");
    }
    
    return [baseClass, ...additionalClasses].join(" ");
  };
 
  // Calcular duración para mostrar
  const calculateDuration = () => {
    if (!data.startTime || !data.endTime) return "";
   
    const [startHours, startMinutes] = data.startTime.substring(0, 5).split(':').map(Number);
    const [endHours, endMinutes] = data.endTime.substring(0, 5).split(':').map(Number);
   
    const startTotalMinutes = startHours * 60 + startMinutes;
    const endTotalMinutes = endHours * 60 + endMinutes;
   
    const durationMinutes = endTotalMinutes - startTotalMinutes;
   
    // Formatear la duración
    if (durationMinutes >= 60) {
      const hours = Math.floor(durationMinutes / 60);
      const minutes = durationMinutes % 60;
      return minutes > 0 ? `${hours}h ${minutes}min` : `${hours}h`;
    }
    return `${durationMinutes} min`;
  };
 
  // Formatear la hora de inicio y fin para mostrar
  const formatTimeDisplay = () => {
    if (!data.startTime || !data.endTime) return "";
    return `${data.startTime.substring(0, 5)} - ${data.endTime.substring(0, 5)}`;
  };

  // Determinar nivel de detalle según la duración
  const isShortDuration = durationMinutes < 20;
  const isCompactSize = durationMinutes < 30;
  
  // Para celdas muy cortas, mostrar información mínima
  if (isVeryShort) {
    return (
      <div className={getCellClass()} onClick={onClick} title={`${data.clientName || 'Sin nombre'} - ${formatTimeDisplay()}`}>
        <div className="booking-info minimal">
          <div className="client-name-mini">{data.clientName ? data.clientName.substring(0, 10) + (data.clientName.length > 10 ? '...' : '') : 'Reserva'}</div>
        </div>
      </div>
    );
  }
  
  return (
    <div className={getCellClass()} onClick={onClick}>
      <div className={`booking-info ${isShortDuration ? 'short-duration' : ''} ${isCompactSize ? 'compact-size' : ''}`}>
        <div className="booking-time">{formatTimeDisplay()}</div>
        <div className="client-name">{data.clientName || 'Sin nombre'}</div>
       
        {/* Solo mostrar estos elementos si hay espacio suficiente */}
        {!isShortDuration && (
          <>
            <div className="reservation-code">{data.reservationCode || 'Sin código'}</div>
            {!isCompactSize && (
              <div className="pricing-info">{data.pricingInfo || `Duración: ${calculateDuration()}`}</div>
            )}
          </>
        )}
       
        {!isShortDuration && data.assignedKarts && data.assignedKarts.length > 0 && (
          <div className="kart-container">
            {data.assignedKarts.map(kart => (
              <span key={kart} className="kart-badge">{kart}</span>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

CalendarCell.propTypes = {
  data: PropTypes.shape({
    status: PropTypes.string,
    clientName: PropTypes.string,
    reservationCode: PropTypes.string,
    pricingInfo: PropTypes.string,
    assignedKarts: PropTypes.arrayOf(PropTypes.string),
    startTime: PropTypes.string,
    endTime: PropTypes.string,
    positionPercent: PropTypes.number,
    durationPercent: PropTypes.number
  }),
  onClick: PropTypes.func,
  proportionalHeight: PropTypes.bool,
  durationMinutes: PropTypes.number,
  exceedsHour: PropTypes.bool,
  isVeryShort: PropTypes.bool
};

CalendarCell.defaultProps = {
  proportionalHeight: false,
  durationMinutes: 60,
  exceedsHour: false,
  isVeryShort: false
};

export default CalendarCell;