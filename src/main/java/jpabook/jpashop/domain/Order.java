package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 배송지

    // order_date로 생성 자바의 캐멀표기를 언더스코프로 바꾼다.
    private LocalDateTime orderDate; // 주문 날짜

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [order,cancel]

    //  == 연관관계 편의 메소드 ==
    public void setmember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    //  == 연관관계 편의 메소드 ==
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    //  == 연관관계 편의 메소드 ==
    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // == 생성 메서드(편의메서드를 다 넣어 호출하면 한번에 되는 것), set보다 좋음. 생성할때만 한번에 쓰면 됨
    // 그래서 연관관계 편의 메서드는 많이 쓰는 엔티티에다 놓는다. == //
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();

        order.setmember(member);
        order.setDelivery(delivery);

        for(OrderItem items : orderItems){
            order.addOrderItem(items);
        }
        // 초기화 ORDER로 강제해놓았음.
        order.setStatus(OrderStatus.ORDER);
//       // 현재 시간 초기화
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // == 비즈니스 로직 == //
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능 합니다.");
        }
        // 주문 취소
        this.setStatus(OrderStatus.CANCEL);

        // 주문 취소 되면서 재고를 원복시키 것
        for(OrderItem items : this.orderItems){
            items.cancel();
        }
    }


    // == 조회 로직 == //
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;

        for(OrderItem items : this.orderItems){
            totalPrice += items.getTotalPrice();
        }

        return totalPrice;
    }
}

