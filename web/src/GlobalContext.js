import React from 'react';

const GEOKEY = process.env.REACT_APP_GEOKEY;
const GEOURL = process.env.REACT_APP_GEOURL;

export const GlobalContext = React.createContext();

export const GlobalStorage = ({ children }) => {

  const [data, setData] = React.useState([]);
  const [listProducts, setListProducts] = React.useState([]);
  const [openCart, setOpenCart] = React.useState(false);
  const [openObs, setOpenObs] = React.useState(false);
  const [obs, setObs] = React.useState(null);
  const [idObs, setIdObs] = React.useState(null);
  const [cart, setCart] = React.useState([]);
  const [total, setTotal] = React.useState(0);
  const [user, setUser] = React.useState(null);
  const [typeBuy, setTypeBuy] = React.useState('');
  const [cep, setCep] = React.useState('');
  const [number, setNumber] = React.useState('');
  const [complement, setComplement] = React.useState('');
  const [typePayment, setTypePayment] = React.useState('');
  const [address, setAddress] = React.useState(null);
  const [order, setOrder] = React.useState('');

  function addCart(item) {
    item.quantity = 1;
    item.currentPrice = item.price;
    item.isSelected = true;
    setCart(oldArray => [...oldArray, item]);
  }

  // add note to cart item
  function addObs(id) {
    let indexItem = cart.map((e) => e.id).indexOf(id);
    let updatedCart = [...cart];
    updatedCart[indexItem].obs = obs;
    setCart(updatedCart);
  }

  function incrementItem(item) {
    let indexItem = cart.map((e) => e.id).indexOf(item.id);
    let updatedCart = [...cart];
    updatedCart[indexItem].quantity = updatedCart[indexItem].quantity + 1;
    updatedCart[indexItem].currentPrice = updatedCart[indexItem].currentPrice + updatedCart[indexItem].price;
    setCart(updatedCart);
  }

  function decrementItem(item) {
    let indexItem = cart.map((e) => e.id).indexOf(item.id);
    let updatedCart = [...cart];
    updatedCart[indexItem].quantity = updatedCart[indexItem].quantity - 1;
    updatedCart[indexItem].currentPrice = updatedCart[indexItem].currentPrice - updatedCart[indexItem].price;
    if (updatedCart[indexItem].quantity === 0) {
      updatedCart[indexItem].isSelected = false;
      updatedCart.splice(indexItem, 1);
      setCart(updatedCart);
    }
    setCart(updatedCart);
  }

  const getProducts = async (category) => {
    const response = await fetch(`http://localhost:8081/products/category/${category}`);
    const json = await response.json();
    setData(json);
  }

  async function getCep(cep) {
    const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const json = await response.json();
    if (json !== undefined) {
      getLocation(json);
      setAddress({
        rua: json.logradouro,
        cidade: json.localidade,
        bairro: json.bairro,
        uf: json.uf,
        cep: json.cep
      });
    }
  }

  async function getLocation(data) {
    const response = await fetch(`${GEOURL}/json?key=${GEOKEY}&q=${data.logradouro}%2C%20${data.bairro}%2C%20${data.localidade}%20-%20${data.uf}%2C${data.cep}%2C%20Brazil&key=03c48dae07364cabb7f121d8c1519492&no_annotations=1&language=en`);
    const json = await response.json();

  }
  // wait for the CEP to be filled in with 8 digits to make the request
  React.useEffect(() => {
    if (cep.length >= 8) {
      getCep(cep);
    }
  }, [cep])

  // watch the cart changes to calculate the total price.
  React.useEffect(() => {
    if (cart.length > 0) {
      const prices = cart.map((item) => item.currentPrice);
      setTotal(prices.reduce((a, b) => a + b));
    }
  }, [cart]);

  // save the address in localStorage after finalizing the order
  React.useEffect(() => {
    if (typeBuy === 'delivery') {
      window.localStorage.setItem('address', JSON.stringify(order.address));
    }
  }, [order]);

  React.useEffect(() => {
    setListProducts(data);
  }, [data]);

  // loads product list and default localStorage address
  React.useEffect(() => {
    async function loadData() {
      const response = await fetch('http://localhost:8081/products/');
      const json = await response.json();
      setData(json);
    }
    loadData();
    const addressDefault = window.localStorage.getItem('address');
    if (addressDefault !== '' && addressDefault !== null && addressDefault !== undefined) {
      setUser(JSON.parse(addressDefault));
    } else {
      setUser(null);
    }
  }, []);

  return (
    <GlobalContext.Provider value={{ getProducts, listProducts, addCart, cart, total, incrementItem, decrementItem, user, setUser, typeBuy, setTypeBuy, cep, setCep, number, setNumber, complement, setComplement, typePayment, setTypePayment, address, setAddress, order, setOrder, openCart, setOpenCart, openObs, setOpenObs, idObs, setIdObs, obs, setObs, addObs }}>
      {children}
    </GlobalContext.Provider>
  );
};