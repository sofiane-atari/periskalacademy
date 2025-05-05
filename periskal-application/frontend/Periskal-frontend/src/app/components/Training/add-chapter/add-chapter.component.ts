import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-add-chapter',
  imports: [],
  templateUrl: './add-chapter.component.html',
  styleUrl: './add-chapter.component.css'
})
export class AddChapterComponent implements OnInit {
  moduleId: number = 0;

  constructor(private route: ActivatedRoute, private router: Router) { }
  ngOnInit(): void {
    this.moduleId = +this.route.snapshot.url[1].path;
  }
}
