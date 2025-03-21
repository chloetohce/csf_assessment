package vttp.batch5.csf.assessment.server.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import vttp.batch5.csf.assessment.server.model.MenuItem;
import vttp.batch5.csf.assessment.server.model.PaymentResponse;


@Repository
public class OrdersRepository {
  @Autowired
  private MongoTemplate mongoTemplate;

  // TODO: Task 2.2
  // You may change the method's signature
  // Write the native MongoDB query in the comment below
  //
  //  Native MongoDB query here
  //
  /*
   *  db.menus.find()
   *    .sort({name: 1})
   */
  public List<MenuItem> getMenu() {
    Query q = Query.query(new Criteria())
      .with(Sort.by(Sort.Direction.ASC, "name"));

    List<Document> result = mongoTemplate.find(q, Document.class, "menus");

    return result.stream()
      .map(d -> {
        MenuItem i = new MenuItem();
        i.setId(d.getString("_id"));
        i.setName(d.getString("name"));
        i.setPrice(d.getDouble("price"));
        i.setDescription(d.getString("description"));

        return i;
      })
      .toList();
  }

  // TODO: Task 4
  // Write the native MongoDB query for your access methods in the comment below
  //
  //  Native MongoDB query here
  /*
   *  db.orders.insert({
   *    _id: <order_id>,
   *    order_id: <order_id>,
   *    payment_id: <payment id>,
   *    username: <username>,
   *    total: <total>,
   *    timestamp: <date>,
   *    items: [
   *      {id: <id>, price: <price>, quantity: <qty>}, ...
   *    ]
   *  })
   */
  public void saveOrders(JsonObject order, double total, PaymentResponse payment) {
    Document toInsert = new Document()
      .append("_id", payment.getOrderId())
      .append(("order_id"), payment.getOrderId())
      .append("payment_id", payment.getPaymentId())
      .append("username", order.getString("username"))
      .append("total", total)
      .append("timestamp", new Date(payment.getTimestamp()))
      .append("items", convertToList(order.getJsonArray("items")));
    mongoTemplate.insert(toInsert, "orders");
  }

  private List<BasicDBObject> convertToList(JsonArray items) {
    List<BasicDBObject> list = new ArrayList<>();
    for (int i = 0; i < items.size(); i++) {
      JsonObject item = items.getJsonObject(i);
      BasicDBObject object = BasicDBObject.parse(item.toString());
      list.add(object);
    }
    return list;
  }
  
}
