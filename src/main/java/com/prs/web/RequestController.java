package com.prs.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.Request;
import com.prs.db.RequestRepository;
import com.prs.db.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/requests")
public class RequestController {

	@Autowired
	private RequestRepository requestRepo;
	@Autowired
	private UserRepository userRepo;
	
	// List - List all Requests
	@GetMapping("/")
	public JsonResponse listRequests() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Get - Gets a Request by ID
	@GetMapping("/{id}")
	public JsonResponse getRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Add - Adds a New Request
	@PostMapping("/")
	public JsonResponse addRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			// Set status to new and submitted date to now
			r.setStatus("New");
			r.setSubmittedDate(LocalDateTime.now());
			
			jr = JsonResponse.getInstance(requestRepo.save(r));
		}
		catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// Update - Updates a Request
	@PutMapping("/")
	public JsonResponse updateRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(r.getId())) {
				jr = JsonResponse.getInstance(requestRepo.save(r));
			} else {
				jr = JsonResponse.getInstance("Error updating request. Request " + r.getId() + " does not exist.");
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
	
	// Delete - Deletes a Request
	@DeleteMapping("/{id}")
	public JsonResponse deleteRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if(requestRepo.existsById(id)) {
				requestRepo.deleteById(id);
				jr = JsonResponse.getInstance("Request " + id + " successfully deleted.");
			}
			else {
				jr = JsonResponse.getInstance("Error deleting request. Request " + id + " does not exist.");
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
	
	@PutMapping("/submit-review")
	public JsonResponse submitRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		
		try {
			if (r.getTotal() <= 50) {
				r.setStatus("Approved");
				r.setSubmittedDate(LocalDateTime.now());
				jr = JsonResponse.getInstance(requestRepo.save(r));
			}
			else {
				r.setStatus("Review");
				r.setSubmittedDate(LocalDateTime.now());
				jr = JsonResponse.getInstance(requestRepo.save(r));
			}
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		
		return jr;
	}
	
	@GetMapping("/list-review/{id}")
	public JsonResponse listReview(@PathVariable int id) {
		JsonResponse jr = null;
		
		try {
			// Find all the requests in review
			List<Request> reqs = new ArrayList<>();
			reqs = requestRepo.findAllByStatusAndUserNot("Review", userRepo.findById(id).get());
			
			jr = JsonResponse.getInstance(reqs);	
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	@PutMapping("/approve")
	public JsonResponse approve(@RequestBody Request r) {
		JsonResponse jr = null;
		
		try {
			r.setStatus("Approved");
			jr = JsonResponse.getInstance(requestRepo.save(r));
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		
		return jr;
	}
	
	@PutMapping("/reject")
	public JsonResponse reject(@RequestBody Request r) {
		JsonResponse jr = null;
		
		try {
			r.setStatus("REJECTED");
			jr = JsonResponse.getInstance(requestRepo.save(r));
		}
		catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		
		return jr;
	}
}
