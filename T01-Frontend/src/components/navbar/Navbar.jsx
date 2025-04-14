import React, { useState } from 'react';
import { FaSearch, FaUser, FaChevronDown } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <nav className="navbar">
      <div className="logo-placeholder">Logo</div>
      <div className="search-container">
        <FaSearch className="search-icon" />
        <input type="text" placeholder="Buscar" className="search-input" />
      </div>

      <div className="nav-links">
        <Link to="/">Inicio</Link>
        <Link to="/">Favoritos</Link>
        <Link to="contact">Contacto</Link>
        <Link to="/" className="arriendo-btn">Poner en arriendo</Link>
      </div>

      <div className="user-menu" onClick={toggleMenu}>
        <FaUser className="user-icon" />
        <FaChevronDown className={`dropdown-arrow ${menuOpen ? 'open' : ''}`} />
        {menuOpen && (
          <div className="dropdown-menu">
            <Link to="/login">Iniciar sesión</Link>
            <Link to="/register">Registrarse</Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
