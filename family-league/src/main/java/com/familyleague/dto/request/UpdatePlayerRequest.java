package com.familyleague.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePlayerRequest {

    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    private Integer jerseyNumber;
}
