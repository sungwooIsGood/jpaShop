package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor /* final 있는 생성자만 주입해준다 */
public class MemberService {

    private final MemberRepository memberRepository;

/*    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    //    회원 가입
    @Transactional
    public Long join(Member member){

//        같은 이름을 가진 회원은 가입 인됨 =>검사
        validateDuplicateMember(member);
        memberRepository.save(member);
//        PK인 id값을 반환하면 쓸곳이 있기 때문에 반환
//        DB에 들어가는 시점이 아니어도 영속성컨택스트에서 키값을 반환해주며 Id값을 채워준다.
        return member.getId();
    }


    // 중복 회원 검사 로직
    private void validateDuplicateMember(Member member) {
        // EXCEPTION
//        애초에 이름을 UNIQUE 조건을 걸어두고 로직으로 한번 더 막아주면 된다.
        List<Member> fingMembers = memberRepository.findByName(member.getName());

        if(!fingMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

//    회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

//    회원 한명 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
