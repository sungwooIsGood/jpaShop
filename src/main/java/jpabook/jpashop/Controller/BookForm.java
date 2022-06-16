package jpabook.jpashop.Controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class BookForm {

    /**
     * Item 공통속성
     * */
    private Long id; // 상품 id 필요

    @NotEmpty(message = "상품이름을 필수로 적어주세요.")
    private String name; // 상품 이름

    private int stockQuantity; // 재고
    private int price;
    /**
     * Book속성 다른 속성은 사용 안하기 때문에 뺌
     * */

    private String author;
    private int isbn;

}
