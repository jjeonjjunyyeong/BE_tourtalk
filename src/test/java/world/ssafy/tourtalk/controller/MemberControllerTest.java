package world.ssafy.tourtalk.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import world.ssafy.tourtalk.model.dto.enums.Gender;
import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.service.MemberService;
import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;

@SpringBootTest(properties = {
		  "jwt.secret=this_is_a_very_secure_and_long_enough_secret_key_for_hs256"
		})
@Transactional
public class MemberControllerTest {

	@Autowired
	private MemberService mService;
	
	@Autowired
	private MemberController memberController;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	private MemberRequest makeUser(int mno) {
        return MemberRequest.builder()
                .mno(mno)
        		.id("testUser")
                .password("1234")
                .nickname("testUser")
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .points(0)
                .email("test@user.com")
                .phone("01012345678")
                .gender(Gender.MAN)
                .address("서울시")
                .postalCode("12345")
                .birthDate(LocalDate.of(1995, 5, 5))
                .build();
    }

	private MemberRequest makeCurator(int mno) {
        return MemberRequest.builder()
        		.mno(mno)
                .id("testCurator")
                .password("1234")
                .nickname("큐레이터")
                .role(Role.CURATOR)
                .status(MemberStatus.ACTIVE)
                .points(0)
                .email("curator@test.com")
                .phone("01098765432")
                .gender(Gender.WOMAN)
                .address("서울특별시")
                .postalCode("54321")
                .birthDate(LocalDate.of(1990, 1, 1))
                .curatorNo("C-2025-001")
                .curatorImg("profile_img.png")
                .adGrade(1)
                .approvedAt(LocalDate.now().atStartOfDay())
                .build();
    }
	
	@Test
    void regist_user_success() {
        MemberRequest request = makeUser(123);
        boolean result = mService.regist(request);
        assertThat(result).isTrue();
    }

    @Test
    void regist_curator_success() {
        MemberRequest request = makeCurator(321);
        boolean result = mService.regist(request);
        assertThat(result).isTrue();
    }
    
    
	/*
	 * @Test void me_from_token_cookie() { MemberRequest request = makeUser(12345);
	 * mService.regist(request); String token =
	 * jwtTokenProvider.createToken(request.getMno(), request.getId(),
	 * request.getNickname(), request.getRole());
	 * 
	 * MockHttpServletRequest servletRequest = new MockHttpServletRequest();
	 * servletRequest.setCookies(new Cookie("token", token));
	 * 
	 * ResponseEntity<?> response = memberController.me(servletRequest);
	 * assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); MemberResponse
	 * body = (MemberResponse) response.getBody();
	 * assertThat(body.getId()).isEqualTo(request.getId()); }
	 */

    @Test
    void update_user_success() {
        MemberRequest request = makeUser(4321);
        mService.regist(request);

        MemberRequest updated = MemberRequest.builder()
                .mno(request.getMno())
                .nickname("업데이트된닉")
                .email("new@email.com")
                .phone("01011112222")
                .build();

        boolean result = mService.update(updated);
        assertThat(result).isTrue();

        MemberResponse after = mService.me(request.getMno());
        assertThat(after.getNickname()).isEqualTo("업데이트된닉");
        assertThat(after.getEmail()).isEqualTo("new@email.com");
    }

    @Test
    void delete_user_success() {
        MemberRequest request = makeUser(1234);
        mService.regist(request);

        boolean deleted = mService.softDelete(request.getMno());
        assertThat(deleted).isTrue();

        MemberResponse after = mService.me(request.getMno());
        assertThat(after.getStatus()).isEqualTo(MemberStatus.DELETED);
    }
}