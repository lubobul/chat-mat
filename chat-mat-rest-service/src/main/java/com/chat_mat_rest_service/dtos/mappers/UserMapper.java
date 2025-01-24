package com.chat_mat_rest_service.dtos.mappers;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDto> {
    // No need for extra methods, MapStruct will generate implementations for the interface
}
