
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CoursePrerequisitesService } from '../../services/course-prerequisites';
import { forkJoin, switchMap } from 'rxjs';
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

  readonly courseCategories = [
  {
    name: 'General Education Coursework',
    codes: [
      'AMH2010', 'AMH2020', 'POS2041',
      'ENC1101', 'ENC1101C', 'ENC1102',
      'SPC2017', 'SPC2065', 'SPC2608',
      'STA2023', 'AST1002', 'BSC1005',
      'BSC2010C', 'CHM1020', 'MGF1130',
      'MUL2010', 'PHI2010', 'PHY1020C',
      'PHY2048C', 'PHY2053C', 'THE2000'
    ]
  },
  {
    name: 'Lower Division Coursework – 30 Credits',
    codes: [
      'CGS1100C', 'CIS2321C', 'CNT2001C',
      'CET2600C', 'COP1000C', 'COP2551C',
      'COP2360C', 'COP2800C', 'COP2842C',
      'CTS1120C', 'CTS1131C', 'CTS1133C',
      'CTS2437C', 'CEN2071C'
    ]
  },
  {
    name: 'Upper Division Coursework – 12 Credits',
    codes: [
      'GEB3213', 'ISM3113C',
      'ISM4117C', 'ISM4212C'
    ]
  },
  {
    name: 'Application Development – 27 Credits',
    codes: [
      'COP3330C', 'COP3813C', 'COP4655C',
      'COP4847C', 'CEN3024C', 'CEN3083C',
      'CEN4025C', 'CEN4802C', 'CEN4940'
    ]
  }
];

getCoursesByCategory(codes: string[]): Course[] {
  return this.availableCourses.filter(course =>
    codes.includes(
      course.code.replace(/\s+/g, '').toUpperCase()
    )
  );
}

get otherCourses(): Course[] {
  const assignedCodes = this.courseCategories.flatMap(
    category => category.codes
  );

  return this.availableCourses.filter(course =>
    !assignedCodes.includes(
      course.code.replace(/\s+/g, '').toUpperCase()
    )
  );
}

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

  this.validationMessage =
    'Loading prerequisites from backend...'
  const completedCoursesRequest = this.http.get<{
    userID: number;
  }>('/api/v1/auth/me').pipe(
   switchMap(user =>
      this.http.get<{
        studentID: number;
        completedCourses: string[];
      }>(`/api/students/${user.userID}/completedCourses`)
  )
);

  const prerequisiteRequests = this.plannedCourses.map(course =>
    this.prerequisitesService.getPrerequisites(
      course.code.replace(/\s+/g, '')
    )
  );

  forkJoin({
    student: completedCoursesRequest,
    prerequisites: forkJoin(prerequisiteRequests)
  }).subscribe({
    next: ({ student, prerequisites }) => {

      this.completedCourses = student.completedCourses.map(
        code => code.replace(/\s+/g, '').toUpperCase()
      );

      this.validationMessage = prerequisites.map(response => {

        const groups = response.prerequisiteGroups;

        if (!Array.isArray(groups)) {
          return `${response.courseID}: Prerequisite rules unavailable.`;
        }

        if (groups.length === 0) {
          return `${response.courseID}: No prerequisites required.`;
        }

        const missingGroups = groups.filter(group =>
          !group.some(code =>
            this.completedCourses.includes(
              code.replace(/\s+/g, '').toUpperCase()
            )
          )
        );

        if (missingGroups.length === 0) {
          return `${response.courseID}: Prerequisites Met`;
        }

        const missing = missingGroups.map(group =>
          group.length > 1
            ? `(${group.join(' OR ')})`
            : group[0]
        ).join(' AND ');

        return `${response.courseID}: Prerequisites Not Met - Missing: ${missing}`;

      }).join('\n');

      this.cdr.detectChanges();
    },

    error: (error) => {
      console.error('Prerequisite validation failed:', error);

      this.validationMessage =
        'Unable to load completed courses or prerequisites.';

      this.cdr.detectChanges();
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
