package jpabook.jpashop.repository.order.simpleQuery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    /**
     * Order 타입으로 받아도되지만
     * JPA가 잘 식별을 못하기 때문에 하나씩 파라미터로 넣어야한다.
     * */
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
       this.orderId = orderId;
       this.name = name;
       this.orderDate = orderDate;
       this.orderStatus = orderStatus;
       this.address = address;
    }
}
