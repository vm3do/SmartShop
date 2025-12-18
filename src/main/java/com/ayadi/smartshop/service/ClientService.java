package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.CreateClientRequest;
import com.ayadi.smartshop.dto.request.UpdateClientRequest;
import com.ayadi.smartshop.dto.response.ClientResponse;
import com.ayadi.smartshop.entity.Client;

import java.util.List;

public interface ClientService {
    ClientResponse createClient(CreateClientRequest request);
    ClientResponse getClient(Long id);
    List<ClientResponse> getAllClients();
    ClientResponse updateClient(Long id, UpdateClientRequest request);
    void deleteClient(Long id);
    Client findClientById(Long id);
}