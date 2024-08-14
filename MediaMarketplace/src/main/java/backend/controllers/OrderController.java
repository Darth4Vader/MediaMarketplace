package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.OrderDto;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.PurchaseOrderException;
import backend.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService; 
	
	@GetMapping("/get/orders")
	public List<OrderDto> getUserOrders() {
		return orderService.getUserOrders();
    }
	
	@GetMapping("/place_order")
    public Long placeOrder() throws PurchaseOrderException, EntityNotFoundException {
		try {
			return orderService.placeOrder();
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new PurchaseOrderException("Unable to purchase the movie", e);
		}
    }
}
