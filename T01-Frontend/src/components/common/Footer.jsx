import React from 'react';
import { FaPhone, FaEnvelope, FaClock } from 'react-icons/fa';
import { Link, useLocation } from 'react-router-dom';
import './Footer.css';
import logo from '../../assets/logo.svg';

const Footer = () => {
  return (
    <footer className="intranet-footer">
      <div className="footer-content">
        {/* Sección de información de contacto */}
        <div className="footer-section">
          <h4 className="footer-title">Contacto Operacional</h4>
          <div className="contact-info">
            <div className="contact-item">
              <FaPhone className="contact-icon" />
              <span>+562 2232 5678</span>
            </div>
            <div className="contact-item">
              <FaEnvelope className="contact-icon" />
              <span>tingeso@kartingrm.cl</span>
            </div>
            <div className="contact-item">
              <FaClock className="contact-icon" />
              <span>Lun-Vie: 14:00 - 22:00</span>
            </div>
          </div>
        </div>

        {/* Sección de enlaces internos */}
        <div className="footer-section">
          <h4 className="footer-title">Accesos Rápidos</h4>
          <ul className="internal-links">
            <li><Link to="/">Manual de Usuario</Link></li>
            <li><Link to="/">Soporte Técnico</Link></li>
            <li><Link to="/">Respaldos del Sistema</Link></li>
          </ul>
        </div>

        {/* Logo institucional */}
        <div className="footer-logo">
          <img src={logo} alt="KartingRM Admin" className="footer-logo-img" />
          <p>Sistema de Gestión Interna</p>
        </div>
      </div>

      {/* División */}
      <div className="footer-divider"></div>

      {/* Copyright y versión */}
      <div className="footer-bottom">
        <span>© 2025 KartingRM - Todos los derechos reservados</span>
      </div>
    </footer>
  );
};

export default Footer;