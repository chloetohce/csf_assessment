package vttp.batch5.csf.assessment.server.repositories;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonObject;
import vttp.batch5.csf.assessment.server.model.PaymentResponse;
import vttp.batch5.csf.assessment.server.repositories.queries.RestaurantQuery;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository {
    @Autowired
    private JdbcTemplate template;

    public boolean verifyUser(String username, String password) {
        SqlRowSet rs = template.queryForRowSet(RestaurantQuery.GET_CUSTOMER, username, password);
        if (rs.next()) {
            return rs.getInt("count") > 0;
        } else {
            return false;
        }
    }

    public void saveOrderPaymentDetails(JsonObject order, double total, PaymentResponse payment) {
        Date orderDate = new Date(payment.getTimestamp());
        
        template.update(RestaurantQuery.INSERT_ORDER, payment.getOrderId(), payment.getPaymentId(),
            orderDate, total, order.getString("username"));
    }

}
