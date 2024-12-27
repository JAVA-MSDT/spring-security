package com.clothesshop.controller;

import com.clothesshop.model.Customer;
import com.clothesshop.model.Order;
import com.clothesshop.repository.CustomerRepository;
import com.clothesshop.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/customers")
public class CustomerRestController {

  private final CustomerRepository customerRepository;
  private final OrderRepository orderRepository;

  public CustomerRestController(CustomerRepository customerRepository, OrderRepository orderRepository) {
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
  }


  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    Iterable<Customer> customersIterable = this.customerRepository.findAll();
    List<Customer> customers = new ArrayList<>();
    customersIterable.forEach(customers::add);
    customers.sort(new Comparator<Customer>() {
      @Override
      public int compare(Customer o1, Customer o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<?> getUser(@PathVariable("id") long customerId, Model model) {
    Optional<Customer> customer = this.customerRepository.findById(customerId);
    if (customer.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "entity not found"
      );
    }
    return new ResponseEntity<>(customer, HttpStatus.OK);
  }

  @GetMapping(path = "/{id}/orders")
  public ResponseEntity<?> getUserOrders(@PathVariable("id") long customerId) {
    Optional<Customer> customer = this.customerRepository.findById(customerId);
    if (customer.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "entity not found"
      );
    }
    Iterable<Order> ordersIterable = this.orderRepository.findAllByCustomerId(customer.get().getId());
    List<Order> orders = new ArrayList<>();
    ordersIterable.forEach(orders::add);
    return new ResponseEntity<>(orders, HttpStatus.OK);
  }
}