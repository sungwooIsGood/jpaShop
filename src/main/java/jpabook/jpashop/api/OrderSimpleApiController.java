package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simpleQuery.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(Lazy로 바꿔준 것들) 즉, 지연로딩 최적화를 위한 코드
 * Order
 * Order -> Member
 * Order -> Delivery
 * */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /**
     * 아래와 같이 Order엔티티 직접 노출X
     * json member -> order바라보고 order -> member 무한 루프에 빠진다.
     * 양방향 관계에 한쪽은 @jsonIgnore를 넣어야한다.(하지만 jsonIgnore만 가지고도 안돈다.)
     * Hibernate5Module 라이브러리 추가한 후 JpashopApplication에 빈등록을 한다.
     * */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        for(Order order : all){
            // Lazy 강제 초기화 => order.getMember()까지는 프록시, getName()이 진짜 객체
            order.getMember().getName();
            order.getDelivery().getAddress();
        }

        return all;
    }

    /**
     * 엔티티를 직접 노출 안시킴
     * 하지만 리턴타입을 List 타입으로 조회하면 안됨.
     * Lazy Loding로 인해 쿼리문이 많이 나가는 문제 발생.(N+1문제 발생)
     * */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 패치 조인 사용 => Order를 찾을 때 연관된 객체 그래프를 같이 참조.
     * */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        // findAllWithMemberDelivery() => member,delivery를 패치조인시킨 메서드
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        /**
         * Dto 같이 중요하지 않은 객체에 엔티티를 담아도 된다.
         * */
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy 초기화 => DB 쿼리 발생시킨다.
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화 => DB 쿼리 발생시킨다.
        }
    }
}
