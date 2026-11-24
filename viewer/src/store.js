import { create } from 'zustand';
import AuthService from "./api.auth.js";

const useAuthStore = create((set) => ({
  isAuth: false,
  isAuthInProgress: false,


  async register(username, email, password) {
    try {
      await AuthService.register(username, email, password);
      await AuthService.login(email, password);
     } catch (err) {
      console.log("register error");
     }
  },

  async login(email, password) {
    set({isAuthInProgress: true});
    try {
      await AuthService.login(email, password);
      set({isAuth: true});

     } catch (err) {
      console.log(err);
      console.log("login error");
     } finally {
      set({isAuthInProgress: false});
    } 
  },

  async checkAuth() {
    set({isAuthInProgress: true});
    try {
      await AuthService.refresh();
      set({isAuth: true});
     } catch (err) {
      console.log("login error");
     } finally {
      set({isAuthInProgress: false});
    } 
  },

  async logout() {
    set({isAuthInProgress: true});
    try {
      await AuthService.logout();
      set({isAuth: false});
    } catch (err) {
      console.log("logout error");
    } finally {
      set({isAuthInProgress: false});
    } 
  }
}));

export default useAuthStore;
