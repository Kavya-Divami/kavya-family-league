package com.familyleague.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLeagueRequest {

    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
