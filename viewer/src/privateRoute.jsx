import { Navigate, Outlet, Route } from "react-router-dom";
import useAuthStore from "./store.js";

const PrivateRoute = (props) => {
  const { isAuth, isAuthInProgress } = useAuthStore((state) => state);

  if (isAuthInProgress) {
    return <div>Checking auth...</div>;
  }
  if (isAuth) {
     return <Outlet/>
  } else {
    return <Navigate to="/login" />;
  }
};
  
export default PrivateRoute;
