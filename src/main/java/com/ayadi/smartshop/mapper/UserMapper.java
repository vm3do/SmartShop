package com.ayadi.smartshop.mapper;

import com.ayadi.smartshop.dto.response.UserResponse;
import com.ayadi.smartshop.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}