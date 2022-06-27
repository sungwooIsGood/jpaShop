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

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 이 방법은 옳바른 방법이 아니다 엔티티로 파라미터 절대 금지
     * */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemverV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 조회
     * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemverV2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();

        member.setName(request.getName());
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
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
     * 조회 DTO
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
