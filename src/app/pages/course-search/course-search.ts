
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-course-search',
  imports: [FormsModule],
  templateUrl: './course-search.html',
  styleUrl: './course-search.css'
})
export class CourseSearch implements OnInit {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  searchText = '';
  selectedCourse: any = null;

  prerequisiteCodes: string[] = [];

readonly applicationDevelopmentCodes = [
  'COP3330C',
  'COP3813C',
  'COP4655C',
  'COP4847C',
  'CEN3024C',
  'CEN3083C',
  'CEN4025C',
  'CEN4802C',
  'CEN4940'
];

  courses = [
    {
      code: 'COP 4847C',
      title: 'Advanced Web Application Development',
      credits: 3,
      description: 'Advanced web application development concepts.',
      prerequisites: 'To be verified'
    },
    {
      code: 'CEN 4940',
      title: 'Application Development Capstone',
      credits: 3,
      description: 'Capstone project involving application development.',
      prerequisites: 'To be verified'
    }
  ];

  ngOnInit(): void {
  this.http.get<any[]>('/api/courses').subscribe({
    next: (data) => {
      this.courses = data.map(course => ({
        code: course.courseCode,
        title: course.title || course.courseCode,
        credits: course.creditHours ?? 0,
        description: course.description || '',
        prerequisites: 'To be verified'
      }));

      this.cdr.detectChanges();
    },
    error: (error) => {
      console.error('Error loading courses:', error);
      this.cdr.detectChanges();
    }
  });
}

get filteredCourses() {
  const query = this.searchText
    .toLowerCase()
    .replace(/\s+/g, '');

  return this.courses.filter(course => {
    const code = course.code.toLowerCase().replace(/\s+/g, '');
    const title = course.title.toLowerCase().replace(/\s+/g, '');

    const isApplicationDevelopment =
      this.applicationDevelopmentCodes.includes(code.toUpperCase());

    const isSelectedCourse =
      this.selectedCourse?.code === course.code;

    if (!query) {
      return isApplicationDevelopment || isSelectedCourse;
    }

    return code.includes(query) || title.includes(query);
  });
}

 
showDetails(course: any) {
  this.selectedCourse = course;
  this.prerequisiteCodes = [];
 
  course.prerequisites = 'Loading prerequisites...';

  const courseCode = course.code.replace(/\s+/g, '');

  this.http.get<{
    courseID: string;
    Prerequisites: string[];
  }>(`/api/courses/${encodeURIComponent(courseCode)}/prerequisites`)
    .subscribe({
     next: (response) => {
  this.prerequisiteCodes = response.Prerequisites ?? [];
const alternativeRule = course.description?.match(
  /Prerequisites:\s*\(([A-Z]{3}\d{4}[A-Z]?(?:\s+or\s+[A-Z]{3}\d{4}[A-Z]?)+)\)/i
);

if (alternativeRule) {
  this.prerequisiteCodes = alternativeRule[1].split(/\s+or\s+/i);
}
course.prerequisites = this.prerequisiteCodes.length > 0
  ? this.prerequisiteCodes.join(', ')
  : course.description?.match(/Prerequisites:\s*([^.]+)/i)?.[1]
    || 'No prerequisites listed';

  this.cdr.detectChanges();
},
      error: (error) => {
  console.error('Error loading prerequisites:', error);
  this.prerequisiteCodes = [];
  course.prerequisites = 'Unable to load prerequisites';
  this.cdr.detectChanges();
}
    });
}
getPrerequisiteLabel(code: string): string {
  const course = this.courses.find(c =>
    c.code.replace(/\s+/g, '').toUpperCase() ===
    code.replace(/\s+/g, '').toUpperCase()
  );

  const formattedCode = code.replace(/^([A-Za-z]{3})(?=\d)/, '$1 ');

  return course
    ? `${formattedCode} – ${course.title} (Credit Hours: ${course.credits})`
    : formattedCode;
}
openPrerequisite(code: string) {
  const prerequisite = this.courses.find(
    course =>
      course.code.replace(/\s+/g, '').toUpperCase() ===
      code.replace(/\s+/g, '').toUpperCase()
  );

 if (prerequisite) {
  this.searchText = prerequisite.code;
  this.showDetails(prerequisite);
} else {
  alert('Course details are not available for ' + code);
}
  }
}
