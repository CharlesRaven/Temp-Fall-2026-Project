
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DegreeProgress {
  studentID: number;
  degreeName: string;
  totalCreditsRequired: number;
  totalCreditsCompleted: number;
  GPA: number;
  Status: string;
}

@Injectable({
  providedIn: 'root'
})
export class DegreeProgressService {
  private http = inject(HttpClient);

  private apiUrl = '/api/students';

  getDegreeProgress(
    studentID: number
  ): Observable<DegreeProgress> {
    return this.http.get<DegreeProgress>(
      `${this.apiUrl}/${studentID}/degreeProgress`
    );
  }
}
