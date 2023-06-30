import React from 'react';
import styles from './ProductItem.module.css';

const ProductItem = ({ inputCart, item }) => {
  return (
    <div className={`${styles.card} ${styles.enterLeft} ${item.isSelected && styles.disable}`} key={item.id} onClick={!item.isSelected ? () => inputCart(item) : () => false}>
      {item.offer && <div className={styles.offer}><p>Oferta</p></div>}
      <p className={styles.description}>{item.farm ? item.farm.name : item.name}</p>
      <img src={item.product ? item.product.img : item.img} alt={item.name} width="80" />
      <div>
        <h3 className={styles.title}>{ item.product ? item.product.name : item.name}</h3>
        <p className={styles.description}>disponiveis: {item.product ? item.product.stock : item.stock} uni</p>
        <p className={styles.description}>{item.product ? item.product.description : item.description}</p>
        <p className={styles.price}>{item.product ? item.product.price.toLocaleString('pt-br',{style: 'currency', currency: 'BRL'}) : item.price.toLocaleString('pt-br',{style: 'currency', currency: 'BRL'})}</p>
      </div>
    </div>
  );
};

export default ProductItem;