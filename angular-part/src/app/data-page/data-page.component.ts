import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-data-page',
  templateUrl: './data-page.component.html',
  styleUrls: ['./data-page.component.css']
})
export class DataPageComponent implements OnInit {

  data: string[];
  url: string;
  errorS: string = null;

  constructor(private route: ActivatedRoute, private http: HttpClient) { }

  ngOnInit(): void {
    this.route
        .data
        .subscribe(params => {
          this.url = params['url'];
          this.fetchData();
        });
  }

  fetchData() : void {
    this.http.get<string[]>(this.url).subscribe(
      response => {
        this.data = response;
      },
      error => this.errorS = error.message);
  }
}
