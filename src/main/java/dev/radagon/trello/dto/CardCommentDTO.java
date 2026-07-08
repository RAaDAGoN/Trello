package dev.radagon.trello.dto;

import lombok.Data;

@Data
public class CardCommentDTO {
    private Long id;
    private Long cardId;
    private Long userId;
    private String comment;
    private String createdAt;
}
