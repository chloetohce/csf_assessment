import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable, OnDestroy, OnInit } from "@angular/core";
import { MenuItem, OrderItem, PaymentResponse } from "./models";
import { BehaviorSubject, Observable, Subject, Subscription } from "rxjs";
import { Router } from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class RestaurantService implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private router = inject(Router)
  
  private orders = new BehaviorSubject<MenuItem[]>([])
  private qty = new BehaviorSubject<number>(0);
  private totalCost = new BehaviorSubject<number>(0);
  private successfulPayment = new BehaviorSubject<PaymentResponse | undefined>(undefined)
  private qtySub!: Subscription
  private totalSub!: Subscription

  orders$: Observable<MenuItem[]> = this.orders.asObservable()
  qty$: Observable<number> = this.qty.asObservable()
  totalCost$: Observable<number> = this.totalCost.asObservable()
  successfulPayment$ = this.successfulPayment.asObservable();

  constructor() {
    this.qtySub = this.orders$.subscribe(orders =>
      this.qty.next(orders.reduce((qty, item) => qty + item.quantity, 0))
    )

    this.totalSub = this.orders$.subscribe(orders => 
      this.totalCost.next(orders.reduce((total, item) => total + (item.quantity * item.price), 0))
    )
  }

  ngOnInit(): void {
  }

  // TODO: Task 2.2
  // You change the method's signature but not the name
  getMenuItems() {
    return this.http.get<MenuItem[]>('/api/menu', {
      headers: new HttpHeaders().append('Accept', 'application/json')
    })
  }

  addOrder(item: MenuItem) {
    const oldOrders: MenuItem[] = this.orders.getValue();
    const itemInBasket: MenuItem | undefined = oldOrders.find(val => val.id === item.id)
    // const idx = oldOrders.findIndex(val => val.id === item.id)
    if (itemInBasket) {
      const newQty: MenuItem = {
        ...itemInBasket,
        quantity: itemInBasket.quantity + 1
      }
      const newOrders = oldOrders.filter(val => val.id !== item.id)
      newOrders.push(newQty)
      this.orders.next(newOrders)
    } else {
      const toAdd = {
        ...item,
        quantity: 1
      }
      const newOrders = [...oldOrders, toAdd];
      this.orders.next(newOrders)
    }
  } 

  removeOrder(id: string) {
    const oldOrders: MenuItem[] = this.orders.getValue();
    const itemInBasket: MenuItem | undefined = oldOrders.find(val => val.id === id)
    if (itemInBasket){
      const newItem: MenuItem = {
        ...itemInBasket,
        quantity: itemInBasket.quantity - 1
      }
      const newOrders = oldOrders.filter(val => val.id !== id)
      newOrders.push(newItem)
      this.orders.next(newOrders.filter(val => val.quantity !== 0))
    }
  }

  // TODO: Task 3.2
 
  sendOrder(username: string, password: string) {
    const payload = {
      username: username,
      password: password,
      items: this.orders.getValue().map(item => ({
        id: item.id,
        price: item.price,
        quantity: item.quantity
      }) as OrderItem)
    }
    console.log(payload)
    return this.http.post<PaymentResponse>('/api/food_order', payload, {
      headers: new HttpHeaders()
        .append('Content-Type', 'application/json')
        .append('Accept', 'application/json')
    })
      .subscribe({
        next: (msg) => {
          this.router.navigate(['/confirmation'])
          this.successfulPayment.next(msg)
        },
        error: (err) => alert(err.error.message)
      })
  }

  reset() {
    this.orders.next([])
  }

  ngOnDestroy(): void {
    this.totalSub.unsubscribe();
    this.qtySub.unsubscribe();
  }
}
