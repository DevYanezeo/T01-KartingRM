import React, { useState, useEffect } from 'react';
import { FaMoneyBillWave, FaPercentage, FaPlus, FaEdit, FaTrash, FaSearch, FaSync, FaExclamationTriangle } from 'react-icons/fa';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import './Tarifas.css';

const Tarifas = () => {
  // Estados para tarifas
  const [pricings, setPricings] = useState([]);
  const [newPricing, setNewPricing] = useState({
    laps: '',
    basePrice: '',
    totalDuration: ''
  });
  const [editingPricingId, setEditingPricingId] = useState(null);
  
  // Estados para descuentos
  const [discounts, setDiscounts] = useState([]);
  const [newDiscount, setNewDiscount] = useState({
    discountType: 'GROUP_SIZE',
    percentage: '',
    minGroupSize: '',
    maxGroupSize: '',
    minVisits: '',
    maxVisits: ''
  });
  const [editingDiscountId, setEditingDiscountId] = useState(null);
  
  // Estados comunes
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [activeTab, setActiveTab] = useState('pricings'); // 'pricings' o 'discounts'
  const [searchTerm, setSearchTerm] = useState('');

  // Fetch tarifas
  const fetchPricings = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8090/api/pricing');
      setPricings(response.data);
    } catch (error) {
      setError('Error al cargar las tarifas');
    } finally {
      setLoading(false);
    }
  };

  // Fetch descuentos
  const fetchDiscounts = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8090/api/discounts');
      setDiscounts(response.data);
    } catch (error) {
      setError('Error al cargar los descuentos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/pricing');
        console.log('Datos recibidos:', response.data); // <-- Añade esto
        setPricings(response.data);
      } catch (error) {
        console.error('Error al cargar:', error);
        setError('Error al cargar las tarifas');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);
  
  // Handlers para tarifas
  const handlePricingChange = (e) => {
    const { name, value } = e.target;
    setNewPricing({
      ...newPricing,
      [name]: value
    });
  };

  const handleAddPricing = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8090/api/pricing', newPricing);
      setPricings([...pricings, response.data]);
      setNewPricing({ laps: '', basePrice: '', totalDuration: '' });
      setSuccessMessage('Tarifa agregada correctamente');
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (error) {
      setError('Error al agregar tarifa');
    }
  };

  const handleEditPricing = (pricing) => {
    setEditingPricingId(pricing.id);
    setNewPricing({
      laps: pricing.laps,
      basePrice: pricing.basePrice,
      totalDuration: pricing.totalDuration
    });
  };

  const handleUpdatePricing = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.put(`http://localhost:8090/api/pricing/${editingPricingId}`, newPricing);
      setPricings(pricings.map(p => p.id === editingPricingId ? response.data : p));
      setEditingPricingId(null);
      setNewPricing({ laps: '', basePrice: '', totalDuration: '' });
      setSuccessMessage('Tarifa actualizada correctamente');
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (error) {
      setError('Error al actualizar tarifa');
    }
  };

  const handleDeletePricing = async (id) => {
    if (window.confirm('¿Estás seguro de eliminar esta tarifa?')) {
      try {
        await axios.delete(`http://localhost:8090/api/pricing/${id}`);
        setPricings(pricings.filter(p => p.id !== id));
        setSuccessMessage('Tarifa eliminada correctamente');
        setTimeout(() => setSuccessMessage(null), 3000);
      } catch (error) {
        setError('Error al eliminar tarifa');
      }
    }
  };

  // Handlers para descuentos
  const handleDiscountChange = (e) => {
    const { name, value } = e.target;
    setNewDiscount({
      ...newDiscount,
      [name]: value
    });
  };

  const handleAddDiscount = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8090/api/discounts', newDiscount);
      setDiscounts([...discounts, response.data]);
      setNewDiscount({
        discountType: 'GROUP_SIZE',
        percentage: '',
        minGroupSize: '',
        maxGroupSize: '',
        minVisits: '',
        maxVisits: ''
      });
      setSuccessMessage('Descuento agregado correctamente');
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (error) {
      setError('Error al agregar descuento');
    }
  };

  const handleEditDiscount = (discount) => {
    setEditingDiscountId(discount.id);
    setNewDiscount({
      discountType: discount.discountType,
      percentage: discount.percentage,
      minGroupSize: discount.minGroupSize || '',
      maxGroupSize: discount.maxGroupSize || '',
      minVisits: discount.minVisits || '',
      maxVisits: discount.maxVisits || ''
    });
  };

  const handleUpdateDiscount = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.put(`http://localhost:8090/api/discounts/${editingDiscountId}`, newDiscount);
      setDiscounts(discounts.map(d => d.id === editingDiscountId ? response.data : d));
      setEditingDiscountId(null);
      setNewDiscount({
        discountType: 'GROUP_SIZE',
        percentage: '',
        minGroupSize: '',
        maxGroupSize: '',
        minVisits: '',
        maxVisits: ''
      });
      setSuccessMessage('Descuento actualizado correctamente');
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (error) {
      setError('Error al actualizar descuento');
    }
  };

  const handleDeleteDiscount = async (id) => {
    if (window.confirm('¿Estás seguro de eliminar este descuento?')) {
      try {
        await axios.delete(`http://localhost:8090/api/discounts/${id}`);
        setDiscounts(discounts.filter(d => d.id !== id));
        setSuccessMessage('Descuento eliminado correctamente');
        setTimeout(() => setSuccessMessage(null), 3000);
      } catch (error) {
        setError('Error al eliminar descuento');
      }
    }
  };

  // Filtrado de datos
  const filteredPricings = pricings.filter(pricing =>
    pricing.laps.toString().includes(searchTerm) ||
    pricing.basePrice.toString().includes(searchTerm) ||
    pricing.totalDuration.toString().includes(searchTerm)
  );

  const filteredDiscounts = discounts.filter(discount =>
    discount.discountType.toLowerCase().includes(searchTerm.toLowerCase()) ||
    discount.percentage.toString().includes(searchTerm) ||
    (discount.minGroupSize && discount.minGroupSize.toString().includes(searchTerm)) ||
    (discount.maxGroupSize && discount.maxGroupSize.toString().includes(searchTerm)) ||
    (discount.minVisits && discount.minVisits.toString().includes(searchTerm)) ||
    (discount.maxVisits && discount.maxVisits.toString().includes(searchTerm))
  );

  return (
    <>
      <Navbar />
      <div className="tarifas-container">
        <h1 className="tarifas-title">Gestión de Tarifas y Descuentos</h1>
        
        {/* Mensajes de éxito y error */}
        {error && (
          <div className="alert alert-error">
            <FaExclamationTriangle /> {error}
            <button onClick={() => setError(null)}>×</button>
          </div>
        )}
        {successMessage && (
          <div className="alert alert-success">
            {successMessage}
            <button onClick={() => setSuccessMessage(null)}>×</button>
          </div>
        )}
        
        {/* Pestañas */}
        <div className="tabs">
          <button
            className={`tab ${activeTab === 'pricings' ? 'active' : ''}`}
            onClick={() => setActiveTab('pricings')}
          >
            <FaMoneyBillWave /> Tarifas
          </button>
          <button
            className={`tab ${activeTab === 'discounts' ? 'active' : ''}`}
            onClick={() => setActiveTab('discounts')}
          >
            <FaPercentage /> Descuentos
          </button>
        </div>
        
        {/* Barra de búsqueda */}
        <div className="search-bar">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder={`Buscar ${activeTab === 'pricings' ? 'tarifas' : 'descuentos'}...`}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button className="refresh-btn" onClick={activeTab === 'pricings' ? fetchPricings : fetchDiscounts}>
            <FaSync /> Actualizar
          </button>
        </div>
        
        {loading ? (
          <div className="loading">Cargando...</div>
        ) : (
          <>
            {activeTab === 'pricings' ? (
              <>
                {/* Formulario de tarifas */}
                <div className="form-container">
                  <h2>{editingPricingId ? 'Editar Tarifa' : 'Agregar Nueva Tarifa'}</h2>
                  <form onSubmit={editingPricingId ? handleUpdatePricing : handleAddPricing}>
                    <div className="form-group">
                      <label>Vueltas:</label>
                      <input
                        type="number"
                        name="laps"
                        value={newPricing.laps}
                        onChange={handlePricingChange}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label>Precio Base:</label>
                      <input
                        type="number"
                        step="0.01"
                        name="basePrice"
                        value={newPricing.basePrice}
                        onChange={handlePricingChange}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label>Duración Total (min):</label>
                      <input
                        type="number"
                        name="totalDuration"
                        value={newPricing.totalDuration}
                        onChange={handlePricingChange}
                        required
                      />
                    </div>
                    <button type="submit" className="btn-submit">
                      {editingPricingId ? 'Actualizar Tarifa' : 'Agregar Tarifa'}
                    </button>
                    {editingPricingId && (
                      <button
                        type="button"
                        className="btn-cancel"
                        onClick={() => {
                          setEditingPricingId(null);
                          setNewPricing({ laps: '', basePrice: '', totalDuration: '' });
                        }}
                      >
                        Cancelar
                      </button>
                    )}
                  </form>
                </div>
                
                {/* Lista de tarifas */}
                <div className="table-container">
                  <h2>Listado de Tarifas</h2>
                  {filteredPricings.length === 0 ? (
                    <p>No hay tarifas registradas</p>
                  ) : (
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Vueltas</th>
                          <th>Precio Base</th>
                          <th>Duración (min)</th>
                          <th>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredPricings.map((pricing) => (
                          <tr key={pricing.id}>
                            <td>{pricing.laps}</td>
                            <td>${pricing.basePrice.toFixed(2)}</td>
                            <td>{pricing.totalDuration}</td>
                            <td className="actions">
                              <button
                                className="btn-edit"
                                onClick={() => handleEditPricing(pricing)}
                              >
                                <FaEdit /> Editar
                              </button>
                              <button
                                className="btn-delete"
                                onClick={() => handleDeletePricing(pricing.id)}
                              >
                                <FaTrash /> Eliminar
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              </>
            ) : (
              <>
                {/* Formulario de descuentos */}
                <div className="form-container">
                  <h2>{editingDiscountId ? 'Editar Descuento' : 'Agregar Nuevo Descuento'}</h2>
                  <form onSubmit={editingDiscountId ? handleUpdateDiscount : handleAddDiscount}>
                    <div className="form-group">
                      <label>Tipo de Descuento:</label>
                      <select
                        name="discountType"
                        value={newDiscount.discountType}
                        onChange={handleDiscountChange}
                        required
                      >
                        <option value="GROUP_SIZE">Tamaño de Grupo</option>
                        <option value="FREQUENT_CLIENT">Cliente Frecuente</option>
                        <option value="BIRTHDAY">Cumpleaños</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label>Porcentaje (%):</label>
                      <input
                        type="number"
                        step="0.01"
                        name="percentage"
                        value={newDiscount.percentage}
                        onChange={handleDiscountChange}
                        required
                      />
                    </div>
                    
                    {newDiscount.discountType === 'GROUP_SIZE' && (
                      <>
                        <div className="form-group">
                          <label>Tamaño Mínimo de Grupo:</label>
                          <input
                            type="number"
                            name="minGroupSize"
                            value={newDiscount.minGroupSize}
                            onChange={handleDiscountChange}
                            required
                          />
                        </div>
                        <div className="form-group">
                          <label>Tamaño Máximo de Grupo:</label>
                          <input
                            type="number"
                            name="maxGroupSize"
                            value={newDiscount.maxGroupSize}
                            onChange={handleDiscountChange}
                            required
                          />
                        </div>
                      </>
                    )}
                    
                    {newDiscount.discountType === 'FREQUENT_CLIENT' && (
                      <>
                        <div className="form-group">
                          <label>Visitas Mínimas:</label>
                          <input
                            type="number"
                            name="minVisits"
                            value={newDiscount.minVisits}
                            onChange={handleDiscountChange}
                            required
                          />
                        </div>
                        <div className="form-group">
                          <label>Visitas Máximas:</label>
                          <input
                            type="number"
                            name="maxVisits"
                            value={newDiscount.maxVisits}
                            onChange={handleDiscountChange}
                            required
                          />
                        </div>
                      </>
                    )}
                    
                    <button type="submit" className="btn-submit">
                      {editingDiscountId ? 'Actualizar Descuento' : 'Agregar Descuento'}
                    </button>
                    {editingDiscountId && (
                      <button
                        type="button"
                        className="btn-cancel"
                        onClick={() => {
                          setEditingDiscountId(null);
                          setNewDiscount({
                            discountType: 'GROUP_SIZE',
                            percentage: '',
                            minGroupSize: '',
                            maxGroupSize: '',
                            minVisits: '',
                            maxVisits: ''
                          });
                        }}
                      >
                        Cancelar
                      </button>
                    )}
                  </form>
                </div>
                
                {/* Lista de descuentos */}
                <div className="table-container">
                  <h2>Listado de Descuentos</h2>
                  {filteredDiscounts.length === 0 ? (
                    <p>No hay descuentos registrados</p>
                  ) : (
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>Tipo</th>
                          <th>Porcentaje</th>
                          <th>Rango</th>
                          <th>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredDiscounts.map((discount) => (
                          <tr key={discount.id}>
                            <td>
                              {discount.discountType === 'GROUP_SIZE' && 'Tamaño Grupo'}
                              {discount.discountType === 'FREQUENT_CLIENT' && 'Cliente Frecuente'}
                              {discount.discountType === 'BIRTHDAY' && 'Cumpleaños'}
                            </td>
                            <td>{discount.percentage}%</td>
                            <td>
                              {discount.discountType === 'GROUP_SIZE' && 
                                `${discount.minGroupSize}-${discount.maxGroupSize} personas`}
                              {discount.discountType === 'FREQUENT_CLIENT' && 
                                `${discount.minVisits}-${discount.maxVisits} visitas`}
                              {discount.discountType === 'BIRTHDAY' && 'N/A'}
                            </td>
                            <td className="actions">
                              <button
                                className="btn-edit"
                                onClick={() => handleEditDiscount(discount)}
                              >
                                <FaEdit /> Editar
                              </button>
                              <button
                                className="btn-delete"
                                onClick={() => handleDeleteDiscount(discount.id)}
                              >
                                <FaTrash /> Eliminar
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              </>
            )}
          </>
        )}
      </div>
      <Footer />
    </>
  );
};

export default Tarifas;