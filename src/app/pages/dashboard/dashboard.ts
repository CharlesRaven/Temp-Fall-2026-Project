import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { switchMap } from 'rxjs';

import {
  DegreeProgress,
  DegreeProgressService
} from '../../services/degree-progress';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private degreeService = inject(DegreeProgressService);
  private cdr = inject(ChangeDetectorRef);
  private http = inject(HttpClient);

  progress: DegreeProgress | null = null;
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    this.http.get<{ userID: number }>('/api/v1/auth/me')
      .pipe(
        switchMap(user =>
          this.degreeService.getDegreeProgress(user.userID)
        )
      )
      .subscribe({
        next: (data) => {
          this.progress = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Degree progress error:', error);

          this.errorMessage =
            error.error?.Message ||
            'Unable to load degree progress. Please log in again or try later.';

          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  get completionPercentage(): number {
    if (!this.progress?.totalCreditsRequired) {
      return 0;
    }

    return Math.min(100, Math.round(
      (this.progress.totalCreditsCompleted /
        this.progress.totalCreditsRequired) * 100
    ));
  }

  get remainingCredits(): number {
    if (!this.progress) {
      return 0;
    }

    return Math.max(
      0,
      this.progress.totalCreditsRequired -
      this.progress.totalCreditsCompleted
    );
  }
}