package com.kms.springboard.member.controller;
import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/joinMember")
    public String showJoinPage(@ModelAttribute(name = "member") MemberDto member) {
        return "users/join";
    }

    @PostMapping("/joinMember")
    public String joinMember(@Valid @ModelAttribute(name = "member") MemberDto memberDto,
                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "users/join";
        }
        try{
            memberService.saveDto(memberDto);
        }catch (IllegalStateException e) {
            bindingResult.rejectValue("userId", "duplicate", "이미 사용 중인 아이디입니다");
            return "users/join";
        }catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("userId", "duplicate", "이미 사용 중인 사용자입니다");
            return "users/join";
        }
        return "redirect:/api";
    }
    @GetMapping("/login")
    public String showLoginForm(@ModelAttribute LoginDto loginDto) {
        return "users/login";
    }



}
