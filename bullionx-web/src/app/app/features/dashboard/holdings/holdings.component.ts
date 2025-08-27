import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HoldingsResponse } from '../../../core/auth.models';

@Component({
  selector: 'bx-holdings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './holdings.component.html',
  styleUrls: ['./holdings.component.css']
})
export class HoldingsComponent {
  @Input() data: HoldingsResponse | null = null;
  @Output() holdingSelected = new EventEmitter<string>();

  onRowClick(symbol: string): void {
    this.holdingSelected.emit(symbol);
  }
}