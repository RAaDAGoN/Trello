package dev.radagon.trello.dto;

import lombok.Data;

@Data
public class BoardColumnDTO {
    private Long id;
    private Long board;
    private String name;
    private int position;
}
