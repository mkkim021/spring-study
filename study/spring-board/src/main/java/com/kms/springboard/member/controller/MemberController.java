package hello.board.member.controller;


import hello.board.member.dto.LoginDto;
import hello.board.member.dto.MemberDto;
import hello.board.member.entity.MemberEntity;
import hello.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/joinMember")
    public String showJoinPage(@ModelAttribute(name = "member") MemberEntity member) {
        return "users/join";
    }

    @PostMapping("/joinMember")
    public String joinMember(@ModelAttribute(name = "member") MemberDto memberDto) {
        memberService.saveDto(memberDto);
        return "redirect:/api";

    }
    @GetMapping("/login")
    public String showLoginForm(@ModelAttribute LoginDto loginDto) {
        return "users/login";
    }
    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto,Model model) {
        boolean login = memberService.isLogin(loginDto);
        if(login){
            return "redirect:/api";
        }
        model.addAttribute("error", "비밀번호가 일치하지 않습니다");
        return "users/login";
    }
}
