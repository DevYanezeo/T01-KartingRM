import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './views/home';  
import Clients from './views/clients';  
import Karts from './views/Karts';
import Tarifas from './views/Tarifas';
import CalendarView from './views/CalendarView';
import Reservas from './views/Reservas';
import Invoice from './views/Invoice';


import './App.css';  

function App() {
  return (
    <Router>
      <div className="app">
        
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/clientes" element={<Clients />} />
            <Route path="/karts" element={<Karts />} />
            <Route path="/tarifas" element={<Tarifas />} />
            <Route path="/calendar" element={<CalendarView />} />
            <Route path="/reservas" element={<Reservas />} />
            <Route path="/invoice" element={<Invoice />} />
          </Routes>
        </main>
        
      </div>
    </Router>
  );
}

export default App;