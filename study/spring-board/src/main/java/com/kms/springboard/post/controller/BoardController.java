package com.kms.springboard.post.controller;

import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.service.BoardService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public String save(@Valid @ModelAttribute BoardDto boardDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "posts/write";
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
                       Model model, Principal principal) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("board", boardDto);
            return "posts/update";
        }
        try{
            boolean ok = boardService.verifyPassword(
                    boardId, boardDto.getPassword(),
                    principal!=null?principal.getName():null);
            if(!ok) {
                bindingResult.rejectValue("password","mismatch","비밀번호가 일치하지 않습니다");
                model.addAttribute("board", boardDto);
            }
            boardService.update(boardId, boardDto);
        }catch(InvalidParameterException e){
            bindingResult.rejectValue("password", "missmatch", e.getMessage());
            model.addAttribute("board", boardDto);
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
