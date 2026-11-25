import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Navbar } from './navbar/navbar';

@Component({
  selector: 'app-layout',
  imports: [Navbar],
  templateUrl: './layout.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './layout.scss',
})
export class Layout {}
