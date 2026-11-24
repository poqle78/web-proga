import useAuthStore from '../store.js';
import { useState } from "react";

export default function AuthForm() {
  const [mode, setMode] = useState('login');
  const [loginName, setLoginName] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [localError, setLocalError] = useState(null);

  const login = useAuthStore((s) => s.login);
  const register = useAuthStore((s) => s.register);
  const isLoading = useAuthStore((s) => s.isAuthInProgress);
  const storeError = useAuthStore((s) => s.error);

  const isLogin = mode === 'login';

  const validate = () => {
    if (!isLogin && name.trim().length < 2) {
      return 'Имя должно быть не короче 2 символов';
    }
    if (!/^\S+@\S+\.\S+$/.test(email) && email.length !== 0) {
      return 'Некорректный email';
    }
    if (password.length < 6) {
      return 'Пароль должен быть не короче 6 символов';
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalError(null);

    const validationError = validate();
    if (validationError) {
      setLocalError(validationError);
      return;
    }

    if (isLogin) {
      await login(loginName, password);
    } else {
      await register(name, email, password);
    }
  };

  const switchMode = (newMode) => {
    setMode(newMode);
    setLocalError(null);
  };

  return (
    <div>
      <div>
        <div onClick={() => switchMode('login')}>
          Вход
        </div>
        <div onClick={() => switchMode('register')}>
          Регистрация
        </div>
      </div>

      {(localError || storeError) && (
        <div>{localError ?? storeError}</div>
      )}

      <form onSubmit={handleSubmit}>
        {!isLogin && (
          <div>
            <input
              type="text"
              placeholder="username"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
        )}
        {!isLogin && (
          <div>
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
        )}
        {isLogin && (
          <div>
            <input
              type="text"
              placeholder="login"
              value={loginName}
              onChange={(e) => setLoginName(e.target.value)}
            />
          </div>
        )}


        <div>
          <input
            type="password"
            placeholder="Пароль"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Загрузка...' : isLogin ? 'Войти' : 'Зарегистрироваться'}
        </button>
      </form>
    </div>
  );
}
