import { Component, OnInit } from '@angular/core';

import { TweetService } from './tweet.service';
import { Tweet } from './tweet';
import { Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-tweet',
  templateUrl: './tweet.component.html',
  styleUrls: ['./tweet.component.css']
})
export class TweetComponent implements OnInit {
  tweets: Tweet[];
  lastSearch: string;

  constructor(private tweetService: TweetService) { }

  ngOnInit() {
  }

  search(query: string) {
    this.lastSearch = query;

    this.tweetService.getTweets(query)
        .subscribe(tweets => this.tweets = tweets);
  }
}
