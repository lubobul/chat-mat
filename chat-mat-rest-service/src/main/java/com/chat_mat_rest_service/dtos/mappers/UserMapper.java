package com.chat_mat_rest_service.dtos.mappers;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDto> {
    @Override
    @Mapping(target = "createdAt", source = "createdAt") // Explicit mapping
    UserDto toDto(User user);

    @Override
    @Mapping(target = "createdAt", source = "createdAt") // Explicit mapping
    User toEntity(UserDto userDto);}
