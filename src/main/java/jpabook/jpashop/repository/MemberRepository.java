package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

//    스프링이 엔티티매니저를 만들어 주입시켜준다.
    @PersistenceContext
    private EntityManager em;

//   회원 정보 저장
    public void save(Member member){
        em.persist(member);
    }

//    세부 회원 조회
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

//    이름으로 회원 조회 JPQL
    public List<Member> findByName(String name){
        String query = "select m from Member m where m.name = :name";
        return em.createQuery(query,Member.class)
                .setParameter("name",name)
                .getResultList();
    }

//    회원 전체 조회
    public List<Member> findAll(){
//        전체조회는 JPQL을 사용
        String query = "select m from member m";
        return em.createQuery(query, Member.class)
                .getResultList();
    }

}
