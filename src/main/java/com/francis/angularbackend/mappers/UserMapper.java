package com.francis.angularbackend.mappers;


import com.francis.angularbackend.dtos.RegisterUserRequest;
import com.francis.angularbackend.dtos.UpdateUserRequest;
import com.francis.angularbackend.dtos.UserDto;
import com.francis.angularbackend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void updateUser(UpdateUserRequest request, @MappingTarget User user);

}
