import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RestaurantService } from '../restaurant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrl: './place-order.component.css'
})
export class PlaceOrderComponent implements OnInit {
  private fb = inject(FormBuilder)
  private service = inject(RestaurantService)
  private router = inject(Router)
  protected form!: FormGroup

  protected orders$ = this.service.orders$;
  protected totalCost$ = this.service.totalCost$;

  // TODO: Task 3

  ngOnInit(): void {
    this.form = this.createForm();
  }

  protected sendOrder() {
    this.service.sendOrder(this.form.get('username')?.value, this.form.get('password')?.value)
  }

  protected resetForm() {
    this.service.reset()
    this.router.navigate([''])
  }

  private createForm(): FormGroup {
    return this.fb.group({
      username: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', Validators.required)
    })
  }

}
