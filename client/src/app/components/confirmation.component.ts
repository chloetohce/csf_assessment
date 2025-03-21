import { Component, inject } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent {
  // TODO: Task 5
  private service = inject(RestaurantService)

  protected successfulPayment$ = this.service.successfulPayment$;
  
  backToMenu() {
    this.service.reset()
  }
}
