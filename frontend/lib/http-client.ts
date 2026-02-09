import axios from 'axios';
import { getToken } from './auth';
import { API_BASE_URL } from './config';

export const http = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      if (typeof window !== 'undefined') {
        localStorage.removeItem('driver_token'); // Ensure token is removed
        window.location.href = '/auth/login';
      }
    }
    return Promise.reject(error);
  }
);

