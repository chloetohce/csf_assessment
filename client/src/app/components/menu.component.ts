import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { map, Observable, take, tap } from 'rxjs';
import { MenuItem } from '../models';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  private service = inject(RestaurantService)
  protected menu$!: Observable<MenuItem[]>;
  protected totalCost$!: Observable<number>;
  protected orderQty$!: Observable<number>;
  protected orders$!: Observable<MenuItem[]>

  
  // TODO: Task 2
  ngOnInit(): void {
    this.menu$ = this.service.getMenuItems();
    this.totalCost$ = this.service.totalCost$;
    this.orderQty$ = this.service.qty$;
    this.orders$ = this.service.orders$;
  }

  getItemQuantity(id: string) {
    return this.orders$.pipe(
      map(orders => orders.find(i => i.id === id)?.quantity ?? 0)
    )
  }

  addOrder(item: MenuItem) {
    this.service.addOrder(item)
  }

  removeOrder(id: string) {
    this.service.removeOrder(id)
  }

}
