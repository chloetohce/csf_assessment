// You may use this file to create any models
export interface MenuItem {
    id: string, 
    name: string,
    price: number,
    description: string,
    quantity: number
}

export interface OrderItem {
    id: string,
    price: number,
    quantity: number
}

export interface PaymentResponse {
    orderId: string,
    paymentId: string, 
    total: number,
    timestamp: number
}