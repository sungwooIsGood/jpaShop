package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class,id);
    }

    // == 검색 == //
    public List<Order> findAll(OrderSearch orderSearch){

//        정적 쿼리 특정 세부 문항을 선택했을 시 하지만 동적쿼리는 적용 어려움.
        return em.createQuery("select o from Order o join o.member m" +
                    " where o.status = :status " +
                    " and m.name like :name", Order.class)
                .setParameter("status",orderSearch.getOrderStatus())
                .setParameter("name",orderSearch.getMemberName())
//                .setFirstResult(1) // 페이징
                .setMaxResults(1000) // 최대 1000건까지 가져오는 것.
                .getResultList();

        // 동적은 Querydsl로 처리
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대
        List<Order> resultList = query.getResultList();

        return resultList;
    }
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }


    public List<Order> findAllWithItem() {
        /**
         * 그냥 쿼리를 내보내면 join되어 데이터가 뻥튀기가 된다.
         * ex) Order테이블에 order_id 4인 값이 있다. OrderItem테이블에 order_id 4가 2개(item을 두개 주문 했기에)있다.
         * order의 id 4는 하나 OrderItem의 id 4는 두개 그래서
         * order테이블에 데이터가 붙기 때문에 order의 값이 두개씩 출력된다.
         * 나는 Order o 테이블의 튜플이 2개만 나오길 원한다. 하지만 4개의 튜플이 생긴다.
         *
         * 결론, 컬렉션을 페치조인 할 시 distinct를 넣어주자.
         * 1. 데이터베이스의 distinct는 모든 데이터가 같아야 중복을 없애주지만,
         * 2. JPA에서는 from 값의 Order ID가 같으면 중복을 제거해준다.
         * */
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }
}
