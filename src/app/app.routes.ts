
import { Routes } from '@angular/router';
import { Dashboard } from './pages/dashboard/dashboard';
import { CourseSearch } from './pages/course-search/course-search';
import { SemesterPlanner } from './pages/semester-planner/semester-planner';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: Dashboard },
  { path: 'courses', component: CourseSearch },
  { path: 'planner', component: SemesterPlanner }
];




