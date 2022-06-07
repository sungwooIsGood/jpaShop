package jpabook.jpashop;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.assertj.core.api.Assertions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
//    @Autowired MemberRepository memberRepository;
//
//
//    @Test
////   springframework 상속
////    test페이지에 @Transactional이 있으면, 테스트가 끝나고 rolllback시킨다.
//    @Transactional
////    @Rollback(false) commit 시키는 것.
//    public void testMember() {
////        given
//        Member member = new Member();
//        member.setUsername("memberA");
//
////        when
////        저장 테스트
//        Long saveId = memberRepository.save(member);
////        조회 테스트
//        Member findMember = memberRepository.find(saveId);
//
////        then
////        isEqualTo => 같은지 확인
//        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
//        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
////        true => 한 트랜젝션 안에서 같은 영속성 컨텍스트의 안에서 id값이 같으면 같은 엔티티라 식별함.
//        Assertions.assertThat(findMember).isEqualTo(member);
//    }
}