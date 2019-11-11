package com.prs.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepository;
import com.prs.db.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("/line-items")
public class LineItemController {

	@Autowired
	private LineItemRepository lineItemRepo;
	@Autowired
	private RequestRepository requestRepo;
	
	// List - List all LineItems
	@GetMapping("/")
	public JsonResponse listLineItems() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Get - Gets a LineItem by ID
	@GetMapping("/{id}")
	public JsonResponse getLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Add - Adds a New LineItem
	@PostMapping("/")
	public JsonResponse addLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.save(li));
			recalculateTotal(li);
		}
		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Update - Updates a LineItem
	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			if (lineItemRepo.existsById(li.getId())) {
				jr = JsonResponse.getInstance(lineItemRepo.save(li));
				recalculateTotal(li);
			} else {
				jr = JsonResponse.getInstance("Error updating lineItem. LineItem " + li.getId() + " does not exist.");
			}
		}
		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Delete - Deletes a LineItem
	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if(lineItemRepo.existsById(id)) {
				LineItem li = lineItemRepo.findById(id).get();
				lineItemRepo.deleteById(id);
				jr = JsonResponse.getInstance("LineItem " + id + " successfully deleted.");
				
				recalculateTotal(li);
			}
			else {
				jr = JsonResponse.getInstance("Error deleting Line Item. LineItem " + id + " does not exist.");
			}
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Recalculate Total - Recalculates total based on changed line item
	private Request recalculateTotal(LineItem li) {
		try {
			// Declare Variables
			double grandTotal = 0;
			double lineTotal = 0;
			List<LineItem> liAll = new ArrayList<>();
			
			// Get the request to update the total for
			// This is getting the whole request for the given line item
			Request r = li.getRequest();
			
			// Get all the line items for a request you just found and put them all in a list
			liAll = lineItemRepo.findAllByRequestId(r.getId());
			
			// Loop through the list and get the line item total, then add that to the grand total
			for (LineItem line: liAll) {
				lineTotal = line.getProduct().getPrice() * line.getQuantity(); // multiply the quantity of the product by the price of the object
				grandTotal += lineTotal;
			}
			
			// return an update of the request after setting the new grand total
			r.setTotal(grandTotal);
			return requestRepo.save(r);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping("/lines-for-pr/{id}")
	public JsonResponse listLineItemsForRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			List<LineItem> lines = new ArrayList<>();
			lines = lineItemRepo.findAllByRequestId(id);
			
			jr = JsonResponse.getInstance(lines);
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
}
