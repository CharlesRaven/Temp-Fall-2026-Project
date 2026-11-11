
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
   
    const query = this.searchText.toLowerCase().trim();

    return this.courses.filter(course =>
      course.code.toLowerCase().includes(query) ||
      course.title.toLowerCase().includes(query)
    );
  }

 
showDetails(course: any) {
  this.selectedCourse = course;
  course.prerequisites = 'Loading prerequisites...';

  const courseCode = course.code.replace(/\s+/g, '');

  this.http.get<{
    courseID: string;
    Prerequisites: string[];
  }>(`/api/courses/${encodeURIComponent(courseCode)}/prerequisites`)
    .subscribe({
      next: (response) => {
        course.prerequisites = response.Prerequisites.length > 0
          ? response.Prerequisites.join(', ')
          : 'No prerequisites listed';

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading prerequisites:', error);
        course.prerequisites = 'Unable to load prerequisites';
        this.cdr.detectChanges();
      }
    });
}

}
