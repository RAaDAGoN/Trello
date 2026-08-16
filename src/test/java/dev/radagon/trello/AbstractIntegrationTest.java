package dev.radagon.trello;


import dev.radagon.trello.dto.BoardColumnDTO;
import dev.radagon.trello.dto.BoardDTO;
import dev.radagon.trello.dto.CardCommentDTO;
import dev.radagon.trello.dto.CardDTO;
import dev.radagon.trello.entity.*;
import dev.radagon.trello.repository.*;
import dev.radagon.trello.service.BoardColumnService;
import dev.radagon.trello.service.BoardService;
import dev.radagon.trello.service.CardCommentService;
import dev.radagon.trello.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractIntegrationTest {
    @Autowired protected UserRepository userRepository;
    @Autowired protected BoardRepository boardRepository;
    @Autowired protected BoardColumnRepository boardColumnRepository;
    @Autowired protected CardRepository cardRepository;
    @Autowired protected CardCommentRepository cardCommentRepository;

    @Autowired protected BoardService boardService;
    @Autowired protected BoardColumnService boardColumnService;
    @Autowired protected CardService cardService;
    @Autowired protected CardCommentService cardCommentService;

    protected User owner;
    protected User intruder;


    @BeforeEach
    void baseSetUp() {
        owner = createUser("owner@test.com",   "Owner",   "owner-public-id");
        intruder = createUser("intruder@test.com", "Intruder", "intruder-public-id");
    }

    protected User createUser(String email, String username, String publicId) {
        User user = new User();
        user.setEmail(email);
        user.setUserName(username);
        user.setPassword("password");
        user.setPublicId(publicId);
        return userRepository.save(user);
    }

    protected Board createBoard(User user, String boardName) {
        BoardDTO dto = new BoardDTO();
        dto.setName(boardName);
        dto.setDescription("This is a test board");
        return boardService.createBoard(dto, user.getId());
    }

    protected BoardColumn createBoardColumn(Board board, User user, String columnName) {
        BoardColumnDTO dto = new BoardColumnDTO();
        dto.setName(columnName);
        return boardColumnService.createColumn(dto, board.getId(), user.getId());
    }

    protected Card createCard(BoardColumn column, User user, String cardName) {
        CardDTO dto = new CardDTO();
        dto.setTitle(cardName);
        dto.setDescription("This is a test card");
        return cardService.createCard(column.getId(), dto, user.getId());
    }

    protected CardComment createCardComment(Long cardId, User user, String cardName) {
        CardCommentDTO dto = new CardCommentDTO();
        dto.setComment(cardName);
        return cardCommentService.createComment(cardId, dto, user.getId());
    }
}
