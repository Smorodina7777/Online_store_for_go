package com.example.online_store_for_go;

import com.example.online_store_for_go.model.Order;
import com.example.online_store_for_go.model.OrderItem;
import com.example.online_store_for_go.model.Product;
import com.example.online_store_for_go.model.Shelf;
import com.example.online_store_for_go.repository.OrderRepository;
import com.example.online_store_for_go.repository.ProductRepository;
import com.example.online_store_for_go.repository.ShelfRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.hibernate.Hibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineStoreForGoApplication {

  private static OrderRepository orderRepository;
  private static ShelfRepository shelfRepository;
  private static ProductRepository productRepository;

  public OnlineStoreForGoApplication(OrderRepository orderRepository,
      ShelfRepository shelfRepository,
      ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.shelfRepository = shelfRepository;
    this.productRepository = productRepository;
  }

  public static void main(String[] args) {
    SpringApplication.run(OnlineStoreForGoApplication.class, args);

    List<Long> orderNumbers = Arrays.asList(10L, 11L, 14L, 15L);
    String orderNumbersAsString = orderNumbers.toString().replace("[", "").replace("]", "");
    System.out.println("> go run main.go " + orderNumbersAsString);
    System.out.println("=+=+=+=\nСтраница сборки заказов " + orderNumbersAsString + "\n");

    Map<String, ArrayList<Map<Product, ArrayList>>> productQuantities = new TreeMap<>();

    for (Long orderNumber : orderNumbers) {
      Order order = orderRepository.findById(orderNumber).orElse(null);
      if (order != null) {
        Hibernate.initialize(order.getOrderItems());

        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
          Product product = orderItem.getProduct();
          int quantity = orderItem.getQuantity();

          Shelf mainShelf = shelfRepository.findFirstByProductAndIsMain(product, true).orElse(null);
          List<Shelf> addShelves = shelfRepository.findShelvesByProductAndIsMain(product, false);

          if (mainShelf != null) {
            ArrayList prodParams;
            productQuantities.putIfAbsent(mainShelf.getName(), new ArrayList<>());
            if (addShelves != null) {
              prodParams = new ArrayList<>(Arrays.asList(quantity, orderNumber, addShelves));
            } else {
              prodParams = new ArrayList<>(Arrays.asList(quantity, orderNumber));
            }
            HashMap<Product, ArrayList> prod = new HashMap<>();
            prod.put(product, prodParams);
            productQuantities.get(mainShelf.getName()).add(prod);
          }
        }
      }
    }

    for (Map.Entry<String, ArrayList<Map<Product, ArrayList>>> entry : productQuantities.entrySet()) {
      String mainShelf = entry.getKey();
      System.out.println("===Стеллаж " + mainShelf);
      ArrayList productsList = entry.getValue();
      for (int i = 0; i < productsList.size(); i++) {
        Map<Product, ArrayList> products = (Map<Product, ArrayList>) productsList.get(i);

        for (Map.Entry<Product, ArrayList> productEntry : products.entrySet()) {
          Product product = productEntry.getKey();
          int quantity = (int) productEntry.getValue().get(0);
          Long orderId = (Long) productEntry.getValue().get(1);
          ArrayList<Shelf> addShelves = (ArrayList<Shelf>) productEntry.getValue().get(2);

          System.out.println(product.getName() + " (id=" + product.getId() + ")");
          System.out.println("заказ " + orderId + ", " + quantity + " шт");
          if (addShelves.size() != 0) {
            System.out.print("доп стеллаж: ");
            for (int j = 0; j < addShelves.size(); j++) {
              if (j != addShelves.size() - 1) {
                System.out.print(addShelves.get(j).getName() + ",");
              } else {
                System.out.print(addShelves.get(j).getName());
              }
            }
            System.out.println();
          }
        }
        System.out.println();
      }
    }
  }
}