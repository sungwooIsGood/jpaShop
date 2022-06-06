package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

//    EntityManagerFactory에서 Manager를 주입을 위한 어노테이션
    @PersistenceContext
    private EntityManager em;

//    저장
    public Long save(Member member){
        em.persist(member);
//        커맨드와 쿼리를 분리하기 위해서
//        사이드 이펙트 예방
//        저장하고 난 후 리턴값은 Id정도만 조회 / Id로 활용할 것들이 있기 때문에
        return member.getId();
    }


//    조회
    public Member find(Long id){
        return em.find(Member.class, id);

    }
}
