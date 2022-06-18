package jpabook.jpashop.Controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("bookform",new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(@Valid BookForm bookform, BindingResult result){

            log.info("오류발생");
        if(result.hasErrors()){
            return "items/createItemForm";
        }
        // set을 이용한게 아닌 생성자메서드를 이용하는게 사이드 이펙트를 줄여준다.
        Book book = new Book();
        book.setName(bookform.getName());
        book.setPrice(bookform.getPrice());
        book.setStockQuantity(bookform.getStockQuantity());
        book.setIsbn(bookform.getIsbn());
        book.setAuthor(bookform.getAuthor());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items",items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        // book만쓰기에 Item으로 안받아 온것
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();

        /**
         * id값은 보안에 취약하다.
         * items/1/edit => {itemId}
         * 누가 임의로 items/2/edit으로 수정하여 들어 올 수도 있다.
         * 때문에 유저가 수정권한을 가진 사람이 맞는지 확인하는 로직이 필요하다.
         * update할 객체자체를 session에다 넣지만 실무에서는 잘 쓰지는 않음.
         * */
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form",form);
        return "items/updateItemForm";

    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId,@ModelAttribute("form") BookForm form, Model model){

        // Item으로 안 넣은 이유 추상메서드 라서
        Book book = new Book();

        /**
         * book객체는 임의로 만든 엔티티로 이미 DB에 한번 저장되어 book에 대한 식별자가 존재한다.
         * book처럼 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티이다.
         * */
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        // 병합(머지) 방식 (book객체 에다 담아 그대로 변경시킨것 => 실무 지양)
        itemService.saveItem(book);

        /**
         * 아래있는 코드들이 더 깔끔하다.
         * 어설프게 객체 생성하여 set한 후 보내는 것보다.
         * 서비스 계층에서 식별자와 변경할 데이터를 명확하게 전달하면 된다.(파라미터 or dto)
         * */
        // 변경감지 방식 (book객체로 담은 후 item객체에다 다시 넣어주었다.)
//        itemService.updateItem(itemId,form.getName(),form.getPrice(), form.getStockQuantity());
//        itemService.updateItem(itemId, updateItemDto itemDto)

        return "redirect:/items";
    }
}
