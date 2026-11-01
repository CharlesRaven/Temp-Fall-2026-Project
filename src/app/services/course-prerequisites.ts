
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PrerequisitesResponse {
  courseID: string;
  Prerequisites: string[];
}

@Injectable({
  providedIn: 'root'
})
export class CoursePrerequisitesService {
  private http = inject(HttpClient);

  getPrerequisites(
    courseID: string
  ): Observable<PrerequisitesResponse> {
    return this.http.get<PrerequisitesResponse>(
      `/api/courses/${encodeURIComponent(courseID)}/prerequisites`
    );
  }
}
