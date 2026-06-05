const CART_KEY = "cart";
const PROMO_KEY = "cart_promotion";

function getCart() {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY)) || [];
  } catch {
    return [];
  }
}

function saveCart(cart) {
  localStorage.setItem(CART_KEY, JSON.stringify(cart));
}

function addDish(dish) {
  const cart = getCart();
  const existing = cart.find((item) => item.dishId === dish.dishId);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({ dishId: dish.dishId, name: dish.name, price: dish.price, quantity: 1 });
  }
  saveCart(cart);
}

function removeDish(dishId) {
  saveCart(getCart().filter((item) => item.dishId !== dishId));
}

function increaseQuantity(dishId) {
  const cart = getCart();
  const item = cart.find((i) => i.dishId === dishId);
  if (item) {
    item.quantity += 1;
    saveCart(cart);
  }
}

function decreaseQuantity(dishId) {
  const cart = getCart();
  const item = cart.find((i) => i.dishId === dishId);
  if (item) {
    if (item.quantity <= 1) {
      removeDish(dishId);
      return;
    }
    item.quantity -= 1;
    saveCart(cart);
  }
}

function setQuantity(dishId, quantity) {
  if (quantity < 1) {
    removeDish(dishId);
    return;
  }
  const cart = getCart();
  const item = cart.find((i) => i.dishId === dishId);
  if (item) {
    item.quantity = quantity;
    saveCart(cart);
  }
}

function clearCart() {
  localStorage.removeItem(CART_KEY);
  localStorage.removeItem(PROMO_KEY);
}

function getTotalItems() {
  return getCart().reduce((sum, item) => sum + item.quantity, 0);
}

function getTotalPrice() {
  return getCart().reduce((sum, item) => sum + item.price * item.quantity, 0);
}

function getPromotion() {
  try {
    return JSON.parse(localStorage.getItem(PROMO_KEY)) || null;
  } catch {
    return null;
  }
}

function savePromotion(promo) {
  localStorage.setItem(PROMO_KEY, JSON.stringify(promo));
}

function removePromotion() {
  localStorage.removeItem(PROMO_KEY);
}

function getFinalPrice() {
  const base = getTotalPrice();
  const promo = getPromotion();
  if (!promo) return base;
  return base - (base * promo.discount) / 100;
}

export {
  getCart,
  saveCart,
  addDish,
  removeDish,
  increaseQuantity,
  decreaseQuantity,
  setQuantity,
  clearCart,
  getTotalItems,
  getTotalPrice,
  getPromotion,
  savePromotion,
  removePromotion,
  getFinalPrice,
};
