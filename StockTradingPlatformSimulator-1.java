import java.util.*;

class InvalidOrderException extends Exception {
    public InvalidOrderException(String message) { super(message); }
}

abstract class Order {
    int id;
    String stock;
    double price;
    int quantity;
    long time;

    Order(int id, String stock, double price, int quantity, long time)
            throws InvalidOrderException {
        if (price <= 0 || quantity <= 0) {
            throw new InvalidOrderException("Invalid price or quantity!");
        }
        this.id = id;
        this.stock = stock;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
    }

    abstract String getType();
}

class BuyOrder extends Order {
    BuyOrder(int id, String stock, double price, int quantity, long time)
            throws InvalidOrderException {
        super(id, stock, price, quantity, time);
    }
    @Override String getType() { return "BUY"; }
}

class SellOrder extends Order {
    SellOrder(int id, String stock, double price, int quantity, long time)
            throws InvalidOrderException {
        super(id, stock, price, quantity, time);
    }
    @Override String getType() { return "SELL"; }
}

public class StockTradingPlatformSimulator {
    static ArrayList<BuyOrder> buyOrders = new ArrayList<>();
    static ArrayList<SellOrder> sellOrders = new ArrayList<>();
    static long clock = 0;
    static int orderId = 1;

    static void placeBuy(String stock, double price, int quantity) {
        try {
            buyOrders.add(new BuyOrder(orderId++, stock, price, quantity, clock++));
            System.out.println("Buy order placed successfully!");
            matchOrders();
        } catch (InvalidOrderException e) {
            System.out.println(e.getMessage());
        }
    }

    static void placeSell(String stock, double price, int quantity) {
        try {
            sellOrders.add(new SellOrder(orderId++, stock, price, quantity, clock++));
            System.out.println("Sell order placed successfully!");
            matchOrders();
        } catch (InvalidOrderException e) {
            System.out.println(e.getMessage());
        }
    }

    static void matchOrders() {
        buyOrders.sort(Comparator.comparingDouble((BuyOrder o) -> o.price)
                .reversed().thenComparingLong(o -> o.time));
        sellOrders.sort(Comparator.comparingDouble((SellOrder o) -> o.price)
                .thenComparingLong(o -> o.time));

        Iterator<BuyOrder> buys = buyOrders.iterator();
        while (buys.hasNext()) {
            BuyOrder buy = buys.next();
            Iterator<SellOrder> sells = sellOrders.iterator();

            while (sells.hasNext()) {
                SellOrder sell = sells.next();
                if (buy.stock.equals(sell.stock) && buy.price >= sell.price) {
                    int traded = Math.min(buy.quantity, sell.quantity);
                    System.out.println("\nTRADE EXECUTED!");
                    System.out.println("Stock: " + buy.stock);
                    System.out.println("Trade Price: " + sell.price);
                    System.out.println("Quantity: " + traded);

                    buy.quantity -= traded;
                    sell.quantity -= traded;
                    if (sell.quantity == 0) sells.remove();
                    if (buy.quantity == 0) {
                        buys.remove();
                        break;
                    }
                }
            }
        }
    }

    static void displayOrders() {
        System.out.println("\n===== BUY ORDER BOOK =====");
        if (buyOrders.isEmpty()) System.out.println("No pending buy orders.");
        for (BuyOrder o : buyOrders)
            System.out.println(o.stock + " | Price: " + o.price + " | Quantity: " + o.quantity);

        System.out.println("\n===== SELL ORDER BOOK =====");
        if (sellOrders.isEmpty()) System.out.println("No pending sell orders.");
        for (SellOrder o : sellOrders)
            System.out.println(o.stock + " | Price: " + o.price + " | Quantity: " + o.quantity);
    }

    public static void main(String[] args) {
        System.out.println("STOCK TRADING PLATFORM");
        System.out.println("----------------------");
        placeBuy("TCS", 3500, 10);
        placeBuy("TCS", 3600, 5);
        placeSell("TCS", 3400, 8);
        placeSell("TCS", 3550, 4);
        displayOrders();
    }
}
