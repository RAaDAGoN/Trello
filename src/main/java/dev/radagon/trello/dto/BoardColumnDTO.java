package dev.radagon.trello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardColumnDTO {
    @NotBlank
    @Size(min = 5, max = 100)
    private String name;
}
