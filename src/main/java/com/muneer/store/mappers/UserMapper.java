package com.muneer.store.mappers;

import com.muneer.store.dtos.RegisterUserRequest;
import com.muneer.store.dtos.UpdateUserRequest;
import com.muneer.store.dtos.UserDto;
import com.muneer.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void update(UpdateUserRequest request, @MappingTarget User user);
}
