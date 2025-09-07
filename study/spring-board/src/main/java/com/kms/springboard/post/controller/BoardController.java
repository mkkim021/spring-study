package com.kms.springboard.post.controller;

import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.service.BoardService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;



    // Main 폼
    @GetMapping
    public String mainForm() {
        return "index";
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
    public String save(@Valid @ModelAttribute BoardDto boardDto,
                       BindingResult bindingResult,
                       Principal principal) {
        if(bindingResult.hasErrors()) {
            return "posts/write";
        }
        if(principal != null) {
            boardDto.setWriter(principal.getName());
        }

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
    public String edit(@PathVariable Long boardId,
                       @Valid @ModelAttribute BoardDto boardDto,
                       BindingResult bindingResult,
                       Principal principal) {
        if(bindingResult.hasErrors()) {
            return "posts/update";
        }
        try{
           boardService.updateWithPassword(
                   boardId,
                   boardDto,
                   boardDto.getPostPassword(),
                   principal !=null ? principal.getName():null
           );
        }catch(AccessDeniedException e){
            bindingResult.rejectValue("password", "mismatch", e.getMessage());
            return "posts/update";
        }
        return "redirect:/api/board";
    }

    //4) Delete
    @PostMapping("/board/delete/{boardId}")
    public String delete(@PathVariable Long boardId, Principal principal) {
        boardService.delete(boardId,principal.getName());
        return "redirect:/api/board";
    }








    /**
     * 테스트 추가 데이터
     */



}
