
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CoursePrerequisitesService } from '../../services/course-prerequisites';
import { forkJoin } from 'rxjs';

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
  private prerequisitesService = inject(CoursePrerequisitesService);
  private cdr = inject(ChangeDetectorRef);
  selectedSemester = 'Spring 2027';
  saveMessage = '';
  validationMessage = '';
  validationChecked = false;

  availableCourses: Course[] = [
    {
      code: 'COP 4847C',
      name: 'Advanced Web Application Development'
    },
    {
      code: 'CEN 4940',
      name: 'Application Development Capstone'
    }
  ];

  plannedCourses: Course[] = [];
  
ngOnInit(): void {
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

        return prerequisites.length > 0
          ? `${response.courseID}: Prerequisites: ${prerequisites.join(', ')}`
          : `${response.courseID}: No prerequisites listed.`;
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
