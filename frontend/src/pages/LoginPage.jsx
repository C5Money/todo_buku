import { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import api from '../api/api';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const registered = location.state?.registered;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/auth/login', { email, password });
      localStorage.setItem('token', res.data.token);
      navigate('/todos');
    } catch (err) {
      setError(err.response?.data?.message || 'Inloggen mislukt');
    }
  };

  return (
    <div className="container">
      <h2>Inloggen</h2>
      {registered && <p className="success">Registratie geslaagd! Je kunt nu inloggen.</p>}
      <form onSubmit={handleSubmit}>
        <input
          className="input"
          type="email"
          placeholder="E-mailadres"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
        />
        <input
          className="input"
          type="password"
          placeholder="Wachtwoord"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />
        {error && <p className="error">{error}</p>}
        <button className="btn-primary" type="submit">Inloggen</button>
      </form>
      <p>Nog geen account? <Link to="/register">Registreren</Link></p>
    </div>
  );
}
