package com.kms.springboard.post.controller;

import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.service.BoardService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    // Main 폼
    @GetMapping
    public String mainForm() {
        return "posts/index";
    }

    // 0) List
    @GetMapping("/board")
    public String board(Model model) {
        List<BoardEntity> boardList = boardService.findByAll();
        model.addAttribute("boardList", boardList);
        return "posts/list";
    }


    //1) Create
    @GetMapping("/board/save")
    public String save() {
        return "posts/write";
    }

    @PostMapping("/board/save")
    public String save(@ModelAttribute BoardDto boardDto) {
        boardService.save(boardDto);
        return "redirect:/api/board";

    }
    //2) Detail
    @GetMapping("/board/{boardId}")
    public String detail(@PathVariable Long boardId, Model model) {
        BoardDto board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "posts/detail";
    }
    //3) Edit
    @GetMapping("/board/update/{boardId}")
    public String edit(@PathVariable Long boardId, Model model) {
        BoardDto board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "posts/update";
    }
    @PostMapping("/board/update/{boardId}")
    public String edit(@PathVariable Long boardId, @ModelAttribute BoardDto boardDto) {
        boardService.update(boardId,boardDto);
        return "redirect:/api/board";
    }

    //4) Delete
    @PostMapping("/board/delete/{boardId}")
    public String delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
        return "redirect:/api/board";
    }






    /**
     * 테스트 추가 데이터
     */

    @PostConstruct
    public void init(){

        String title = "title";
        String content = "content";
        String writer = "writer";
        BoardDto boardDto = new BoardDto();

        boardDto.setTitle(title);
        boardDto.setContent(content);
        boardDto.setWriter(writer);
        boardService.save(boardDto);

    }

}
