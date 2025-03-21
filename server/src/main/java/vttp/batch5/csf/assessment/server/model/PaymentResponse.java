package vttp.batch5.csf.assessment.server.model;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class PaymentResponse {
    private String paymentId;

    private String orderId;

    private long timestamp;

    public static PaymentResponse fromJsonString(String entity) {
        JsonReader reader = Json.createReader(new StringReader(entity));
        JsonObject obj = reader.readObject();

        PaymentResponse pr = new PaymentResponse();
        pr.setPaymentId(obj.getString("payment_id"));
        pr.setOrderId(obj.getString("order_id"));
        pr.setTimestamp(obj.getJsonNumber("timestamp").longValue());

        return pr;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    
    
}
