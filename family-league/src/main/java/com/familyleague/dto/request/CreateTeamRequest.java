package com.familyleague.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 10, message = "Short code must not exceed 10 characters")
    private String shortCode;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;
}
