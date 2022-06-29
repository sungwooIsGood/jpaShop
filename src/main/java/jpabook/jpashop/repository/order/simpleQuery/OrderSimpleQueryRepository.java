package jpabook.jpashop.repository.order.simpleQuery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

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
