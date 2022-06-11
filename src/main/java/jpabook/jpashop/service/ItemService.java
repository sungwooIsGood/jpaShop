package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회 시 true는 성능 최적화 update는 false로 바꾸기
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    
    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }
    
    public List<Item> findItems(){
        return itemRepository.findAll();
    }
    
    public Item findOnd(Long itemId){
        return itemRepository.findOnd(itemId);
    }
}
