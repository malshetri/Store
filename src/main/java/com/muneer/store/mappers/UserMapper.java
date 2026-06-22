package com.muneer.store.mappers;

import com.muneer.store.dtos.UserDto;
import com.muneer.store.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
