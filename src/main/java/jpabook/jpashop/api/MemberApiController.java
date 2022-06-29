package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 조회
     * 이 방법은 추가 api에 대응하지 못한다.
     * */
    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){
        return memberService.findMembers();
    }

    @GetMapping("api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDTO> collect = findMembers.stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());

        // ex) 회원들의 list와 전 회원에서 할인 10% 한다는 의미로 10을 넘겨 보고 싶다.
        return new Result(10,collect);
    }

    /**
     * 조회 DTO
     * */
    @Data
    @AllArgsConstructor
    static class MemberDTO{
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class Result<T> {
        // count는 api 추가 사항에 맞게 추가해본 것이다.
        private int discount;
        private T data;
    }


    /**
     * 이 방법은 옳바른 방법이 아니다 엔티티로 파라미터 절대 금지
     * */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemverV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 가입
     * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemverV2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();

        member.setName(request.getName());
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /**
     * 가입 DTO
     * */
    @Data
    static class CreateMemberRequest{

        @NotEmpty
        private String name;
        // (주소 등)엔티티 내용 생략함


    }

    @Data
    static class CreateMemberResponse{

        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    /**
     * 수정
     * */

    @PutMapping("/api/v3/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdatememberRequest request){

        memberService.update(id,request.getName());
        /** 커맨드와 쿼리를 분리,
         * update()메서드에 반환 타입을 Member로 해도되지만
         * 커맨드와 쿼리(update,delete,insert)를 분리 시키는 것이 좋다. 그래서 다시 쿼리를 호출하는 것
        *  */
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    /**
     * 수정 DTO
     * */
    @Data
    static class UpdatememberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }


}
