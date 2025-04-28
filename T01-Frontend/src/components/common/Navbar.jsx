import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FaMoneyBillWave} from 'react-icons/fa';
import { 
  FaHome,
  FaCalendarAlt,
  FaUsers,
  FaCarAlt,
  FaChartBar,
  FaCog,
  FaChevronDown,
  FaUserCircle,
  FaBell
} from 'react-icons/fa';
import './Navbar.css';

const Navbar = () => {
  const [userDropdown, setUserDropdown] = useState(false);
  const location = useLocation();

  const navItems = [
    { path: '/', icon: <FaHome />, text: 'Dashboard' },
    { path: '/calendar', icon: <FaCalendarAlt />, text: 'Reservas' },
    { path: '/clientes', icon: <FaUsers />, text: 'Clientes' },
    { path: '/karts', icon: <FaCarAlt />, text: 'Flota Karts' },
    { path: '/reportes', icon: <FaChartBar />, text: 'Reportes' },
    { path: '/tarifas', icon: <FaCog />, text: 'Tarifas & Descuentos' },
    { path: '/invoice', icon: <FaMoneyBillWave />, text: 'Comprobantes de Pagos' }
  ];

  return (
    <header className="admin-navbar">
      <div className="navbar-brand">
        <Link to="/" className="logo-link">
          <div className="logo-container">
            <span className="logo-icon">🏎️</span>
            <span className="logo-text">KartingRM Admin</span>
          </div>
        </Link>
      </div>

      <nav className="navbar-main">
        <ul className="nav-list">
          {navItems.map((item) => (
            <li key={item.path} className="nav-item">
              <Link
                to={item.path}
                className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
              >
                <span className="nav-icon">{item.icon}</span>
                <span className="nav-text">{item.text}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>

    </header>
  );
};

export default Navbar;