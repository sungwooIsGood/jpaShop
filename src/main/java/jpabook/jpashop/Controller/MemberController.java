package jpabook.jpashop.Controller;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 메인 홈페이지 home.html에서 get으로 url 주소를 주고 createMemberForm => 가입 시 Post로 주었음.!
    @GetMapping("/members/new")
    public String createForm(Model model){
        // 그냥 바로 createMemberForm으로 넘겨도 되지만 vaildation을 가져간다.
        model.addAttribute("memberForm",new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, Member member, BindingResult result){

        if(result.hasErrors()){
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(),form.getStreet(),form.getZipcode());

        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/"; // 첫번째 페이지로 넘김
    }

    // 회원 목록조회(전체)
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "members/memberList";
    }

}
