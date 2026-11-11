
import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard';
import { CourseSearch } from './pages/course-search/course-search';
import { SemesterPlanner } from './pages/semester-planner/semester-planner';
import { Login } from './pages/login/login';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: Dashboard },
  { path: 'courses', component: CourseSearch },
  { path: 'planner', component: SemesterPlanner },
  { path: 'login', component: Login }
];
