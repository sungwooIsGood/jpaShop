package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
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

}
