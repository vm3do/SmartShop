package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreateClientRequest;
import com.ayadi.smartshop.dto.request.UpdateClientRequest;
import com.ayadi.smartshop.dto.response.ClientResponse;
import com.ayadi.smartshop.entity.Client;
import com.ayadi.smartshop.enums.CustomerTier;
import com.ayadi.smartshop.enums.OrderStatus;
import com.ayadi.smartshop.exception.BusinessException;
import com.ayadi.smartshop.exception.ResourceNotFoundException;
import com.ayadi.smartshop.mapper.ClientMapper;
import com.ayadi.smartshop.repository.ClientRepository;
import com.ayadi.smartshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {
    
    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final ClientMapper clientMapper;
    
    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        Client client = clientMapper.toEntity(request);
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }
    
    public ClientResponse getClient(Long id) {
        Client client = findClientById(id);
        return clientMapper.toResponse(client);
    }
    
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ClientResponse updateClient(Long id, UpdateClientRequest request) {
        Client client = findClientById(id);
        
        if (!client.getEmail().equals(request.getEmail()) && 
            clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }
        
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        Client client = findClientById(id);
        clientRepository.delete(client);
    }
    
    @Transactional
    public void updateClientTier(Long clientId) {
        Client client = findClientById(clientId);
        
        Long confirmedOrders = orderRepository.countByClientIdAndStatus(clientId, OrderStatus.CONFIRMED);
        BigDecimal totalSpent = orderRepository.sumTotalByClientIdAndStatus(clientId, OrderStatus.CONFIRMED);
        
        CustomerTier newTier = calculateTier(confirmedOrders.intValue(), totalSpent);
        client.setTier(newTier);
        clientRepository.save(client);
    }
    
    private CustomerTier calculateTier(int totalOrders, BigDecimal totalSpent) {
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        } else if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        } else if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }
        return CustomerTier.BASIC;
    }
    
    public Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
}