package jpabook.jpashop.repository.order.simpleQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    /**
     * 그냥 Repository는 순수한 엔티티만 조회해야함.
     * 그래서 dto를 조회하는 것이라면 따로 query문을 따로 저장하는 저장소를 만들어 주는게 좋다.
     * repository의 용도가 애매해지는 것을 방지
     * */
    public List<OrderSimpleQueryDto> findOrderDtos() {

        /**
         * Order엔티티를 OrderSimpleQueryDto로 쿼리문으로 매핑 할 수 없다.
         * JPA는 엔티티나 embedded(Address) 타입으로만 반환 가능
         * 때문에 쿼리문에다 new operation을 꼭 써주어야한다.
         * OrderSimpleQueryDto() 안에 파라미터는 이름은 달라도 식별자위치만 맞아도 된다.
         * */
        return em.createQuery("select new jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryDto(" +
                "o.id, m.name, o.orderDate, o.status, d.address" +
                ") " +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d",OrderSimpleQueryDto.class
        ).getResultList();
    }
}
