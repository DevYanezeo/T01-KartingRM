const PricingCard = ({ title, price, features, highlight = false }) => {
    return (
      <div className={`pricing-card ${highlight ? 'highlight' : ''}`}>
        <h3>{title}</h3>
        <div className="price">{typeof price === 'number' ? `$${price.toLocaleString()}` : price}</div>
        <ul>
          {features.map((feature, idx) => (
            <li key={idx}>{feature}</li>
          ))}
        </ul>
      </div>
    );
  };
  
  export default PricingCard;