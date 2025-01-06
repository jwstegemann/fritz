/******/ (() => { // webpackBootstrap
// This entry needs to be wrapped in an IIFE because it needs to be isolated against other entry modules.
(() => {
window.toggleMenu = () => {
    const menu = document.getElementById("mobile-menu");
    const menuIcons = document.getElementsByClassName("mobile-icon")
    if (menu) {
        menu.classList.toggle("-translate-y-[100vh]")
    }
    for(let i = 0; i < menuIcons.length; i++) {
        menuIcons[i].classList.toggle("hidden")
    }
}
})();

// This entry needs to be wrapped in an IIFE because it needs to be isolated against other entry modules.
(() => {
// extracted by mini-css-extract-plugin
})();

/******/ })()
;