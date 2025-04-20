import React from 'react';
import Navbar from '../components/common/Navbar';
import Hero from '../components/common/Hero';
import PricingCard from '../components/reservation/PricingCard';
import Footer from '../components/common/Footer';
import './Home.css'; // O usa Tailwind/MUI styles

const Home = () => {
  // Datos de ejemplo para tarifas y descuentos (RF1-RF4)
  const pricingData = [
    {
      title: "10 Vueltas / 10 Min",
      price: 15000,
      features: [
        "Para 1 persona",
        "Casco incluido",
        "Certificado de tiempo"
      ]
    },
    {
      title: "15 Vueltas / 15 Min",
      price: 20000,
      features: [
        "Para 1 persona",
        "Casco y traje incluido",
        "Análisis de vueltas"
      ],
      highlight: true // Destacar este plan
    },
    {
      title: "Grupo (3-5 personas)",
      price: "10% OFF",
      features: [
        "Descuento por grupo",
        "Carrera simultánea",
        "Foto grupal incluida"
      ]
    }
  ];

  return (
    <div className="home-container">
      <Navbar />
      
      <Hero 
        title="Vive la emoción del karting en RM"
        subtitle="Reserva ahora y obtén descuentos exclusivos"
        ctaText="Reservar Ahora"
        ctaLink="/reservar"
      />

      {/* Sección de Tarifas */}
      <section className="pricing-section">
        <h2 className="section-title">Nuestras Tarifas</h2>
        <div className="pricing-grid">
          {pricingData.map((plan, index) => (
            <PricingCard
              key={index}
              title={plan.title}
              price={plan.price}
              features={plan.features}
              highlight={plan.highlight}
            />
          ))}
        </div>
      </section>

      {/* Sección de Descuentos */}
      <section className="discounts-section">
        <h2 className="section-title">Descuentos Especiales</h2>
        <div className="discounts-grid">
          <div className="discount-card">
            <h3>🎉 Cumpleañeros</h3>
            <p>50% OFF si reservas en tu cumpleaños</p>
          </div>
          <div className="discount-card">
            <h3>🔄 Clientes Frecuentes</h3>
            <p>20% OFF en tu 5ta visita mensual</p>
          </div>
          <div className="discount-card">
            <h3>👨‍👩‍👧‍👦 Grupos Grandes</h3>
            <p>15% OFF para 6+ personas</p>
          </div>
        </div>
      </section>

      {/* Llamado a la acción */}
      <section className="cta-section">
        <h2>¿Listo para la aventura?</h2>
        <a href="/reservar" className="cta-button">Reservar Ahora</a>
      </section>

      <Footer />
    </div>
  );
};

export default Home;