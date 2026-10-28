import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Layout} from './commons/layout/layout';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Layout],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('web');

  constructor(private translate: TranslateService) {
    this.translate.addLangs(['cs', 'en']);
    this.translate.setFallbackLang('cs');
    this.translate.use('cs'); // přepsat na en pro vyzkoušení angličtiny
  }
}
