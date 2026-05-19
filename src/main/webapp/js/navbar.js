const burger = document.querySelector('.burger-button');
burger.addEventListener('click', () => {
  const expanded = burger.getAttribute('aria-expanded') === 'true';
  burger.setAttribute('aria-expanded', String(!expanded));
  document.querySelector('.navbar').classList.toggle('close');
});
