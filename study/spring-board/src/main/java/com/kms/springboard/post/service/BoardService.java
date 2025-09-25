package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

public interface BoardService {
    BoardDto save(BoardDto boardDto);
    /**
 * Retrieve a paginated list of boards.
 *
 * @param pageable pagination and sorting information for the requested page
 * @return a Page of BoardDto representing the requested page of boards
 */
Page<BoardDto> findAll(Pageable pageable);
    /**
 * Retrieves the board identified by the given id.
 *
 * @param id the identifier of the board to retrieve
 * @return the BoardDto representing the board with the specified id
 */
BoardDto findById(Long id);
    /**
 * Deletes the board identified by the given id.
 *
 * @param id the identifier of the board to delete
 */
void delete(Long id);

    /**
 * Update an existing board identified by its ID using the provided update data after verifying the supplied raw password.
 *
 * @param boardId       the ID of the board to update
 * @param updateBoardDto the DTO containing fields to update on the board
 * @param rawPassword   the plaintext password used to authorize the update
 * @throws AccessDeniedException if the password is invalid or the caller is not authorized to perform the update
 */
void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPassword) throws AccessDeniedException;
}
