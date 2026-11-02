
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';

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

  progress: DegreeProgress | null = null;
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    // Demo student ID. Replace with a valid backend student ID.
    const studentID = 5968431;

    this.degreeService.getDegreeProgress(studentID)
      .subscribe({
        next: (data) => {
          this.progress = data;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.errorMessage =
            'Degree progress is not available yet. The backend endpoint needs to be implemented.';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  get completionPercentage(): number {
    if (!this.progress?.totalCreditsRequired) {
      return 0;
    }

    return Math.round(
      (this.progress.totalCreditsCompleted /
        this.progress.totalCreditsRequired) * 100
    );
  }
}
