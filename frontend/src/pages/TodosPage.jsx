import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';

export default function TodosPage() {
  const [todos, setTodos] = useState([]);
  const [newTitle, setNewTitle] = useState('');
  const [editId, setEditId] = useState(null);
  const [editTitle, setEditTitle] = useState('');
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'light');
  const navigate = useNavigate();

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  useEffect(() => {
    api.get('/todos')
      .then(res => setTodos(res.data))
      .catch(() => navigate('/login'));
  }, []);

  const addTodo = async (e) => {
    e.preventDefault();
    if (!newTitle.trim()) return;
    try {
      const res = await api.post('/todos', { title: newTitle.trim() });
      setTodos([...todos, res.data]);
      setNewTitle('');
    } catch (err) {
      alert(err.response?.data?.message || 'Aanmaken mislukt');
    }
  };

  const toggleDone = async (todo) => {
    const res = await api.put(`/todos/${todo.id}`, { title: todo.title, done: !todo.done });
    setTodos(todos.map(t => t.id === todo.id ? res.data : t));
  };

  const startEdit = (todo) => {
    setEditId(todo.id);
    setEditTitle(todo.title);
  };

  const saveEdit = async (id) => {
    const res = await api.put(`/todos/${id}`, { title: editTitle });
    setTodos(todos.map(t => t.id === id ? res.data : t));
    setEditId(null);
  };

  const deleteTodo = async (id) => {
    await api.delete(`/todos/${id}`);
    setTodos(todos.filter(t => t.id !== id));
  };

  const logout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const toggleTheme = () => {
    const next = theme === 'light' ? 'dark' : 'light';
    setTheme(next);
    localStorage.setItem('theme', next);
  };

  return (
    <div className="container">
      <div className="header-row">
        <h2>Mijn taken</h2>
        <div className="header-actions">
          <button className="btn-ghost" onClick={toggleTheme} title="Thema wisselen">
            {theme === 'light' ? '🌙' : '☀️'}
          </button>
          <button className="btn-ghost" onClick={logout}>Uitloggen</button>
        </div>
      </div>

      {todos.length >= 5 ? (
        <p className="max-warning">Je hebt het maximum van 5 taken bereikt.</p>
      ) : (
        <form className="add-form" onSubmit={addTodo}>
          <input
            className="input"
            placeholder="Nieuwe taak..."
            value={newTitle}
            onChange={e => setNewTitle(e.target.value)}
          />
          <button className="btn-primary" type="submit">Toevoegen</button>
        </form>
      )}

      <ul className="todo-list">
        {todos.map(todo => (
          <li key={todo.id} className={`todo-item${todo.done ? ' done' : ''}`}>
            <input
              type="checkbox"
              checked={todo.done}
              onChange={() => toggleDone(todo)}
            />
            {editId === todo.id ? (
              <>
                <input
                  className="input edit-input"
                  value={editTitle}
                  onChange={e => setEditTitle(e.target.value)}
                />
                <button className="btn-primary" onClick={() => saveEdit(todo.id)}>Opslaan</button>
              </>
            ) : (
              <>
                <span className="todo-title">{todo.title}</span>
                <button className="btn-ghost" onClick={() => startEdit(todo)}>Bewerken</button>
              </>
            )}
            <button className="btn-danger" onClick={() => deleteTodo(todo.id)}>×</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
