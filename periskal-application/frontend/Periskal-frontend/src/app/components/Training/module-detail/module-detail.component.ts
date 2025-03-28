import { Component } from '@angular/core';
import { chapter, module, training } from '../training-employee/training-employee.component';
import { ActivatedRoute, Router } from '@angular/router';
import { TrainingService } from '../../../services/Training/training.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-training-detail',
  imports: [TranslatePipe],
  templateUrl: './module-detail.component.html',
  styleUrl: './module-detail.component.css'
})
export class ModuleDetailComponent {
  training: training | undefined;
  modules: module[] = [];
  chapters: chapter[] = [];
  text: string = "";

  trainingId: number = 0;
  moduleId: number = 0;
  activeChapterId: number = 0;

  constructor(private route: ActivatedRoute, private trainingSvc: TrainingService, private router: Router) {
  }


  formatText(text: string): string {
    return text.replace(/\n/g, '<br>');
  }

  ngOnInit(): void {
    // Get the training ID from the URL
    this.trainingId = Number(this.route.snapshot.paramMap.get('id'));
    this.moduleId = Number(this.route.snapshot.paramMap.get('moduleid'))

    // Call the service method to get the products for the specific training
    this.trainingSvc.getTrainingById(this.trainingId).subscribe({
      next: (data) => {
        this.training = data;
        this.modules = data.modules;
        this.chapters = this.modules[this.moduleId - 1]?.chapters ?? [];

        if (this.chapters.length > 0) {
          this.text = this.chapters[this.activeChapterId]?.textChapter?.toString() ?? "";
        }
      },
      error: (error) => {
        console.error('Error fetching training products', error);
      }
    });

  }

  goToChaptersPage(moduleId: number) {
    this.router.navigate(['/training', this.trainingId, 'module', moduleId])
  }

  setActiveChapter(chapterId: number) {
    this.activeChapterId = chapterId;
  }
}
