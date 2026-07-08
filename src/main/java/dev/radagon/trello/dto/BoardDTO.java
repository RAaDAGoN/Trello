package dev.radagon.trello.dto;

import lombok.Data;

@Data
public class BoardDTO {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String createdAt;
}
