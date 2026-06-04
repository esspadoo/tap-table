const CART_KEY = "cart";

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

function clearCart() {
  localStorage.removeItem(CART_KEY);
}

function getTotalItems() {
  return getCart().reduce((sum, item) => sum + item.quantity, 0);
}

function getTotalPrice() {
  return getCart().reduce((sum, item) => sum + item.price * item.quantity, 0);
}

export {
  getCart,
  saveCart,
  addDish,
  removeDish,
  increaseQuantity,
  decreaseQuantity,
  clearCart,
  getTotalItems,
  getTotalPrice,
};
