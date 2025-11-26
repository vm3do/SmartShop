package com.ayadi.smartshop.mapper;

import com.ayadi.smartshop.dto.request.CreateClientRequest;
import com.ayadi.smartshop.dto.request.UpdateClientRequest;
import com.ayadi.smartshop.dto.response.ClientResponse;
import com.ayadi.smartshop.entity.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientResponse toResponse(Client client);
    Client toEntity(CreateClientRequest request);
    Client toEntity(UpdateClientRequest request);
}