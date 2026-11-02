
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-course-search',
  imports: [FormsModule],
  templateUrl: './course-search.html',
  styleUrl: './course-search.css'
})
export class CourseSearch {
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

  get filteredCourses() {
    const query = this.searchText.toLowerCase().trim();

    return this.courses.filter(course =>
      course.code.toLowerCase().includes(query) ||
      course.title.toLowerCase().includes(query)
    );
  }

  showDetails(course: any) {
    this.selectedCourse = course;
  }
}
