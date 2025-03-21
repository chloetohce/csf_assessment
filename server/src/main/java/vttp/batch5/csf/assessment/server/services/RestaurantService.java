package vttp.batch5.csf.assessment.server.services;

import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.csf.assessment.server.exceptions.InvalidUserException;
import vttp.batch5.csf.assessment.server.model.MenuItem;
import vttp.batch5.csf.assessment.server.model.PaymentResponse;
import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;
import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {
  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private RestaurantRepository restaurantRepository;

  private final RestTemplate restTemplate = new RestTemplate();

  private static final String URL_PAYMENT = "https://payment-service-production-a75a.up.railway.app/api/payment";
  private static final String PAYEE = "Toh Cyn Ee Chloe";

  // TODO: Task 2.2
  // You may change the method's signature
  public JsonArray getMenu() {
    List<MenuItem> list = ordersRepository.getMenu();

    JsonArrayBuilder arr = Json.createArrayBuilder();
    list.forEach(i -> {
      JsonObject obj = Json.createObjectBuilder()
          .add("id", i.getId())
          .add("name", i.getName())
          .add("price", i.getPrice())
          .add("description", i.getDescription())
          .build();
      arr.add(obj);
    });
    return arr.build();
  }

  // TODO: Task 4
  public JsonObject saveOrder(String payload) {
    JsonReader reader = Json.createReader(new StringReader(payload));
    JsonObject entity = reader.readObject();
    String username = entity.getString("username");
    String password = entity.getString("password");
    JsonArray items = entity.getJsonArray("items");
    double total = calculateTotal(items);
    String orderId = UUID.randomUUID().toString().substring(0, 8);

    boolean isUserVerified = verifyUser(username, password);

    PaymentResponse payment = processPayment(orderId, username, total);

    restaurantRepository.saveOrderPaymentDetails(entity, total, payment);
    ordersRepository.saveOrders(entity, total, payment);

    return payment.toJson();
  }

  private boolean verifyUser(String username, String password) {
    
    boolean isUserVerified = restaurantRepository.verifyUser(username, password);
    if (!isUserVerified) {
      throw new InvalidUserException("Invalid username and/or password.");
    }
    return isUserVerified;
  }

  private PaymentResponse processPayment(String orderId, String payer, double total) {
    JsonObject payload = Json.createObjectBuilder()
      .add("order_id", orderId)
      .add("payer", payer)
      .add("payee", PAYEE)
      .add("payment", total)
      .build();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("X-Authenticate", payer);

    RequestEntity<String> request = RequestEntity.post(URL_PAYMENT)
      .headers(headers)
      .body(payload.toString());

    ResponseEntity<String> response = restTemplate.exchange(request, String.class);
    return PaymentResponse.fromJsonString(response.getBody());
  }

  private double calculateTotal(JsonArray items) {
    double total = 0;
    for (int i = 0; i < items.size(); i++) {
      JsonObject item = items.getJsonObject(i);
      total += item.getJsonNumber("price").doubleValue();
    }

    return total;
  }

}
