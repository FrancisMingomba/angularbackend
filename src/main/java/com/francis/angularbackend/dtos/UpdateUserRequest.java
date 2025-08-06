package com.francis.angularbackend.dtos;


import lombok.Data;

@Data
public class UpdateUserRequest {
    public String name;
    public String email;
}
