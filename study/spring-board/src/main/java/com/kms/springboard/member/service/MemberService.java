package hello.board.member.service;

import hello.board.member.dto.LoginDto;
import hello.board.member.dto.MemberDto;
import hello.board.member.entity.MemberEntity;
import hello.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;


public interface MemberService {

    MemberEntity save(MemberEntity member);
    MemberEntity saveDto(MemberDto memberDto);
    boolean isLogin(LoginDto loginDto);
    MemberEntity findByUsername(String username);

}
