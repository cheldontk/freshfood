import React from 'react';
import './App.css';
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./Keycloak"
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import PrivateRoute from "./helpers/PrivateRoute";
import Nav from './Components/Nav';
import Home from './Components/Home/Home';
import Checkout from './Components/Checkout/Checkout';
import Finish from './Components/Finish/Finish';
import { GlobalStorage } from './GlobalContext';

function App() {
  return (
    <div>
    <ReactKeycloakProvider authClient={keycloak}>
      <Nav />
      <BrowserRouter>
        <GlobalStorage>
          <Routes>
            <Route path="/*" element={<Home />} />
            <Route path="/checkout" element={
              <PrivateRoute>
                <Checkout />
              </PrivateRoute>
            } />
            <Route path="/completed" element={<Finish />} />
          </Routes>
        </GlobalStorage>
      </BrowserRouter>
    </ReactKeycloakProvider>
    </div>
  );
}

export default App;
