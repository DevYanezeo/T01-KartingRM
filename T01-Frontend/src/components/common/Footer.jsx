import React from 'react';
import { FaLinkedin, FaInstagram, FaXTwitter } from 'react-icons/fa6';
import './Footer.css';
import logo from '../../assets/logo.svg';

const Footer = () => {
  return (
    <footer className="footer-container">
      <div className="footer-content">
        <div className="footer-logo">
          <img src={logo} alt="Logo" className="footer-logo-image" />
        </div>

        <div className="footer-legal-social">
          <div className="footer-legal">
            <h4>Información Legal</h4>
            <ul>
              <li>Términos y condiciones</li>
              <li>Preguntas frecuentes</li>
              <li>Políticas de privacidad</li>
              <li>Políticas de arriendo</li>
            </ul>
          </div>

          <div className="footer-social">
            <h4>Redes Sociales</h4>
            <div className="icons">
              <FaLinkedin />
              <FaXTwitter />
              <FaInstagram />
            </div>
          </div>
        </div>
      </div>

      <hr className="footer-divider" />
      <p className="footer-copy">© 2025 Rental - Todos los derechos reservados</p>
    </footer>
  );
};

export default Footer;
