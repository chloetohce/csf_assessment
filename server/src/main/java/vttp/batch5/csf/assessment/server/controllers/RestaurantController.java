package vttp.batch5.csf.assessment.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import vttp.batch5.csf.assessment.server.exceptions.InvalidUserException;
import vttp.batch5.csf.assessment.server.services.RestaurantService;

@RestController
@RequestMapping("/api")
public class RestaurantController {
  @Autowired
  private RestaurantService service;

  // TODO: Task 2.2
  // You may change the method's signature
  @GetMapping(path="/menu", produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getMenus() {
    JsonArray menu = service.getMenu();
    
    return ResponseEntity.ok(menu.toString());
  }

  // TODO: Task 4
  // Do not change the method's signature
  @PostMapping(path="/food_order", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> postFoodOrder(@RequestBody String payload) {
    try {
      JsonObject receipt = service.saveOrder(payload);

      System.out.println(receipt.toString());

      return ResponseEntity.ok(receipt.toString());
    } catch (InvalidUserException e) {
      JsonObject message = Json.createObjectBuilder()
        .add("message", e.getMessage())
        .build();
      return ResponseEntity.status(401)
        .body(message.toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      JsonObject message = Json.createObjectBuilder()
        .add("message", e.getMessage())
        .build();
      return ResponseEntity.status(500)
        .body(message.toString());
    }
  }
}
