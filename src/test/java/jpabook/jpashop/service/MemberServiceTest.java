package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;

import static org.junit.Assert.*;

// 스프링부트컨테이너를 이용한다는 뜻. 그래서 @Autowired같은걸 쓸 수 있는 것.
@SpringBootTest
// 스프링과 같이 엮어서 테스트 한다는 뜻
@RunWith(SpringRunner.class)
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

//    insert문을 확인하기 위해서
    @Autowired
    EntityManager em;

    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception{
        // given
        Member member = new Member();
        member.setName("kim");

        em.flush();
        // when
        Long savedId = memberService.join(member);

        // then
        assertEquals(member,memberRepository.findOne(savedId));
    }

//    테스트 환경에서 try-catch문과 같이 예외를 잡아주는 속성이 있다.
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        memberService.join(member2);

        // then
//        try {
//            memberService.join(member2); // 예외 발생해야 한다.
//        } catch(IllegalStateException e) {
////            true
//            return;
//        }

//        assert에서 지원하는 fail()메소드는 fail메소드까지 오게 예외 발생
        fail("예외가 발생해야 한다.");
    }
}