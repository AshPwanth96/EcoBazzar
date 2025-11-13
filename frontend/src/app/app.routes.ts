import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Admin } from './pages/admin/admin';
import { Cart } from './pages/cart/cart';
import { AuthGuard } from './guards/auth.guard';
import { Dashboard } from './pages/dashboard/dashboard';
import { RoleGuard } from './guards/role.guard';
import { ProductList } from './pages/product-list/product-list';
import { ProductDetail } from './pages/product-detail/product-detail';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'cart', component: Cart },// ✅ new route
  { path: 'dashboard', component: Dashboard, canActivate: [AuthGuard] },
  {path: 'products', component: ProductList},

  { path: 'products/:id', component: ProductDetail },

  {
    path: 'admin',
    loadComponent: () => import('./pages/admin/admin').then(m => m.Admin),
    canActivate: [RoleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },

  { path: '**', redirectTo: '' }
];
