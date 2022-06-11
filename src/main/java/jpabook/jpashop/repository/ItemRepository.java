package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

//    @PersistenceContext 대신 springData-jpa에서 Autowired를 지원
    private final EntityManager em;
    
    // 저장
    public void save(Item item){
        
        if(item.getId() == null){
            em.persist(item);
        } else{
//          update와 비슷하게 동작
            em.merge(item);
        }
        
    }
    
    // 아이디값으로 하나만 조회
    public Item findOnd(Long id){
        return em.find(Item.class, id);
    }
    
    // 전체조회
    public List<Item> findAll(){
        String query = "select i from Item i";
         return em.createQuery(query,Item.class)
                .getResultList();
    }
    
    
}
