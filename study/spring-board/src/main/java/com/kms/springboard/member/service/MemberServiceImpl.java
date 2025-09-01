package hello.board.member.service;

import hello.board.member.dto.LoginDto;
import hello.board.member.dto.MemberDto;
import hello.board.member.entity.MemberEntity;
import hello.board.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberEntity save(MemberEntity member) {
        return memberRepository.save(member);

    }

    @Override
    public MemberEntity saveDto(MemberDto memberDto) {
        MemberEntity member = MemberEntity.builder()
                .username(memberDto.getUsername())
                .password(memberDto.getPassword())
                .build();
        return memberRepository.save(member);
    }

    @Override
    public boolean isLogin(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        MemberEntity byUsername = memberRepository.findByUsername(username);
        if (byUsername != null) {
            if(byUsername.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MemberEntity findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
}
