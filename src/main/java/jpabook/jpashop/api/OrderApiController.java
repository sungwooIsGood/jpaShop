package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.query.OrderQueryDto;
import jpabook.jpashop.repository.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    /**
     * XToMany
     * */
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();

            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }
    /**
     * 일대다(orderItems), 다대다 관계에서는
     * 1. dto 클래스 생성
     * 2. 페치조인, distinct
     * 3. 하지만!!! 페이징은 안된다.
     * */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        for(Order order : orders){
            System.out.println("order ref= " + order + "  order = "+ order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
    /**
     * 페이징 한계돌파 시
     * 1. 연관된 모든 XToOne -> join fetch로  ex) XToOne(Order) -> XToOne(Member) -> XToOne(people)
     * 2. XToMany -> join fetch를 풀어준다.
     * 3. requestParam으로 페이징 offset과 limit를 받아온다.
     * 4. application.yml 에서 jpa-properties-hibernate 속성 안에 default_batch_fetch_size:int형 속성 추가
     * => 이 옵션은 웬만하면 키는게 좋다.
     * => 작동 원리: 1) join fetch 2) 페이징 처리 3) items는 지연로딩이라 계속 쿼리가 추가적으로 나가야하지만
     * default_batch_fetch_size로 인해 추가 in()쿼리가 나간다. 100이라 적었으면 100개를 한번에 가져와 준다.
     * 총 order 쿼리, items쿼리, item쿼리로 3개 밖에 나가지 않았다.
     *
     * 글로벌이 아닌 세세하게 하고 싶다면 Order객체에 OrderItems 변수에다 @BatchSize(size = 100)
     * OrderItem 안에 Item은 XToOne이기 때문에 Item객체위에 @BatchSize(size = 100)를 적어준다.
     * 하지만 글로벌로 해도 성능상 차이는 없다. size를 어떻게 적냐에 따라 달라진다.
     * 적당한 사이즈가 좋다 100~1000 사이를 선택하는 것이 좋다.
     * 데이터베이스마다 다르지만 1000이상했을 시 오류를 이르키는 것도 있기에 1000까지
     * 순간부하에 대해 was DB가 버틸수 있다면 1000 그렇지 않다면 100 진짜 모르겠다 500
     * 메모리에서 사용량은 같기 때문에 메모리에 대해 생각은 안해도 된다.
     * */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        for(Order order : orders){
            System.out.println("order ref= " + order + "  order = "+ order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @Data
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        /**
         * DTO에 엔티티 관계가 나오면 안댄다
         * 컬렉션 값을 내보내면 안된다. OrderItem => OrderItemDto
         * */
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // List<OrderItem> 일때, 하지만 OrderItem을 직접 받으면 안된다.
//            order.getOrderItems().stream().forEach(o ->o.getItem().getName());
//            orderItems = order.getOrderItems();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto{

        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량
        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }


}
