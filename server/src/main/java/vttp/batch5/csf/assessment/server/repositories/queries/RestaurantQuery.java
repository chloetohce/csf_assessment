package vttp.batch5.csf.assessment.server.repositories.queries;

public class RestaurantQuery {
    public static final String GET_CUSTOMER = """
            select count(*) as count from customers
                where username=? and password=sha2(?, 224);
            """;
    
    public static final String INSERT_ORDER = """
            insert into place_orders (order_id, payment_id, order_date, total, username)
                values(?, ?, ?, ?, ?)
            """;
}
