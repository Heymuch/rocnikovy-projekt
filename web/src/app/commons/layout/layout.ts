import { Component } from '@angular/core';
import {Navbar} from './navbar/navbar';

@Component({
  selector: 'app-layout',
  imports: [
    Navbar
  ],
  templateUrl: './layout.html',
  styleUrl: './layout.scss',
})
export class Layout {}
