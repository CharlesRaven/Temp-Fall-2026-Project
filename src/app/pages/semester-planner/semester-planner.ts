
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CoursePrerequisitesService } from '../../services/course-prerequisites';
import { forkJoin } from 'rxjs';
import { HttpClient } from '@angular/common/http';

interface Course {
  code: string;
  name: string;
}

@Component({
  selector: 'app-semester-planner',
  imports: [],
  templateUrl: './semester-planner.html',
  styleUrl: './semester-planner.css'
})
export class SemesterPlanner implements OnInit {
  completedCourses: string[] = [];
  private prerequisitesService = inject(CoursePrerequisitesService);
  private cdr = inject(ChangeDetectorRef);
  private http = inject(HttpClient);
  selectedSemester = 'Spring 2027';
  saveMessage = '';
  validationMessage = '';
  validationChecked = false;

  availableCourses: Course[] = [];

  plannedCourses: Course[] = [];
  
ngOnInit(): void {

  this.http.get<any[]>('/api/courses').subscribe({
  next: (data) => {
    this.availableCourses = data.map(course => ({
      code: course.courseCode,
      name: course.title
    }));
    this.cdr.detectChanges();
  },
  error: (error) => {
    console.error('Error loading available courses:', error);
  }
});

  const savedPlan = localStorage.getItem('fscjDegreePlan');

  if (savedPlan) {
    try {
      const plan = JSON.parse(savedPlan);

      if (
        typeof plan.semester === 'string' &&
        Array.isArray(plan.courses)
      ) {
        this.selectedSemester = plan.semester;
        this.plannedCourses = plan.courses;
      }
    } catch {
      this.saveMessage = 'Unable to load the previously saved plan.';
    }
  }
}


  addCourse(course: Course): void {
    this.saveMessage = '';
    this.validationChecked = false;
    this.validationMessage = '';
    const exists = this.plannedCourses.some(
      item => item.code === course.code
    );

    if (!exists) {
      this.plannedCourses.push(course);
    }
  }

  removeCourse(course: Course): void {
    this.saveMessage = '';
    this.validationChecked = false;
    this.validationMessage = '';

    this.plannedCourses = this.plannedCourses.filter(
      item => item.code !== course.code
    );
  }
    checkPrerequisites(): void {
  this.validationChecked = true;

  if (this.plannedCourses.length === 0) {
    this.validationMessage =
      'Please add at least one course before checking prerequisites.';
    return;
  }

  this.validationMessage = 'Loading prerequisites from backend...';
  this.http.get<{ studentID: number; completedCourses: string[] }>(
  '/api/students/1/completedCourses'
).subscribe({
  next: (data) => {
  console.log('Completed courses:', data.completedCourses);
  this.completedCourses = data.completedCourses;
},
  error: (error) => {
    console.error('Error loading completed courses:', error);
  }
});

  const requests = this.plannedCourses.map(course =>
    this.prerequisitesService.getPrerequisites(
      course.code.replace(/\s+/g, '')
    )
  );

  forkJoin(requests).subscribe({
    next: (responses) => {
      console.log('Prerequisites received:', responses);
      this.validationMessage = responses.map(response => {
        const prerequisites = response.Prerequisites;
        const missingPrerequisites = prerequisites.filter(
  prerequisite => !this.completedCourses.includes(prerequisite)
);

      if (prerequisites.length === 0) {
  return `${response.courseID}: No prerequisites required.`;
}

if (missingPrerequisites.length === 0) {
  return `${response.courseID}: Prerequisites Met`;
}

return `${response.courseID}: Prerequisites Not Met - Missing: ${missingPrerequisites.join(', ')}`;
      }).join('\n');
      this.cdr.detectChanges();
    },
    error: () => {
      this.validationMessage =
        'Unable to load prerequisites from the backend. Please check that Spring Boot is running.';
    }
  });
}
  savePlan(): void {
  if (this.plannedCourses.length === 0) {
    this.saveMessage = 'Please add at least one course before saving.';
    return;
  }

  const plan = {
    semester: this.selectedSemester,
    courses: this.plannedCourses
  };

  localStorage.setItem('fscjDegreePlan', JSON.stringify(plan));
  this.saveMessage = 'Your semester plan has been saved in this browser.';
}
}
