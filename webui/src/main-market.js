import './styles/app.css'
import App from './pages/market/App.svelte'
import { mount } from 'svelte'

mount(App, { target: document.getElementById('app') })
