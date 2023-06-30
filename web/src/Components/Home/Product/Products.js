import React from 'react';
import { useKeycloak } from "@react-keycloak/web";
import { useParams } from 'react-router-dom';
import { GlobalContext } from '../../../GlobalContext';
import styles from './Products.module.css';
import ProductItem from './ProductItem';

const Products = () => {
  const { keycloak, initialized } = useKeycloak();
  const { getProducts, listProducts, addCart } = React.useContext(GlobalContext);
  const { product } = useParams();

  React.useEffect(() => {
    product && getProducts(product);
  }, [product]);

  function handleClick(product) {
    if(!keycloak.authenticated) keycloak.login(); 
    addCart(product.product ? product.product : product)
  }

  return (
    <div className={styles.products}>
      <div className={styles.container}>
        <h2 className={styles.title}>Produtos perto de você</h2>
        <div className={styles.areaProducts}>
          {listProducts.map((item) => <ProductItem inputCart={handleClick} key={item.id} item={item} /> )}
        </div>
      </div>
    </div>
  );
};

export default Products;