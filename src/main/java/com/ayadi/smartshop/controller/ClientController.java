package com.ayadi.smartshop.controller;

import com.ayadi.smartshop.dto.request.CreateClientRequest;
import com.ayadi.smartshop.dto.request.UpdateClientRequest;
import com.ayadi.smartshop.dto.response.ClientResponse;
import com.ayadi.smartshop.dto.response.OrderResponse;
import com.ayadi.smartshop.service.ClientService;
import com.ayadi.smartshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest request) {
        ClientResponse response = clientService.createClient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
        ClientResponse response = clientService.getClient(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> response = clientService.getAllClients();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, 
                                                        @Valid @RequestBody UpdateClientRequest request) {
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok("Client deleted successfully");
    }
    
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getClientOrders(@PathVariable Long id) {
        List<OrderResponse> response = orderService.getClientOrders(id);
        return ResponseEntity.ok(response);
    }
}