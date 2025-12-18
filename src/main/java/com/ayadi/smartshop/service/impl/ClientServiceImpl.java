package com.ayadi.smartshop.service.impl;

import com.ayadi.smartshop.dto.request.CreateClientRequest;
import com.ayadi.smartshop.dto.request.UpdateClientRequest;
import com.ayadi.smartshop.dto.response.ClientResponse;
import com.ayadi.smartshop.entity.Client;
import com.ayadi.smartshop.exception.ResourceNotFoundException;
import com.ayadi.smartshop.mapper.ClientMapper;
import com.ayadi.smartshop.repository.ClientRepository;
import com.ayadi.smartshop.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    @Override
    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        Client client = clientMapper.toEntity(request);
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }
    
    @Override
    public ClientResponse getClient(Long id) {
        Client client = findClientById(id);
        return clientMapper.toResponse(client);
    }
    
    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional
    public ClientResponse updateClient(Long id, UpdateClientRequest request) {
        Client client = findClientById(id);
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }
    
    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = findClientById(id);
        clientRepository.delete(client);
    }
    
    @Override
    public Client findClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }
}