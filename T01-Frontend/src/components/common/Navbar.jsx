import React, { useState } from 'react';
import { FaSearch, FaUser, FaChevronDown, FaFlagCheckered } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import logo from '../../assets/logo.svg';
import './Navbar.css';

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <nav className="navbar">
      <div className="logo-container">
        <img src={logo} alt="KartingRM Logo" className="logo" />
      </div>

      <div className="nav-links">
        <Link to="/"><FaFlagCheckered className="nav-icon" /> Inicio</Link>
        <Link to="/tracks">Pistas</Link>
        <Link to="/events">Eventos</Link>
        <Link to="/contact">Contacto</Link>
        <Link to="/reservations" className="reservar-btn">Reservar Ahora</Link>
      </div>
    </nav>
  );
};

export default Navbar;