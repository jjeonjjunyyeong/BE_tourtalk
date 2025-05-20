//package world.ssafy.tourtalk.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.transaction.annotation.Transactional;
//
//import jakarta.servlet.http.Cookie;
//import world.ssafy.tourtalk.model.dto.Member;
//import world.ssafy.tourtalk.model.dto.MemberDetails;
//import world.ssafy.tourtalk.model.dto.request.MemberRegistRequest;
//import world.ssafy.tourtalk.model.service.MemberService;
//import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;
//
//@SpringBootTest(properties = {
//		  "jwt.secret=this_is_a_very_secure_and_long_enough_secret_key_for_hs256"
//		})
//@Transactional
//public class AuthControllerTest {
//
//	@Autowired
//	private MemberService mService;
//	
//	@Autowired
//	private AuthController authController;
//	
//	@Autowired
//	private JwtTokenProvider jwtTokenProvider;
//	
//	private Member makeMember(String id) {
//	    return Member.builder()
//	    	.mno(123)
//	        .id(id).password("1234").nickname("test")
//	        .role(Member.Role.USER).status(Member.Status.ACTIVE)
//	        .points(0)
//	        .build();
//	}
//
//	private MemberDetails makeDetails() {
//	    return MemberDetails.builder()
//	        .email("test@user.com").phone("01012348756")
//	        .gender(MemberDetails.Gender.MAN)
//			.address("광주광역시")
//			.postalCode("12345")
//			.birthDate(LocalDate.of(2025, 5, 5))
//	        .build();
//	}
//	
//	@Test
//	void login_success_and_failure() {
//	    // given
//	    String id = "TestUser2";
//	    String pw = "1234";
//	    Member member = Member.builder()
//	            .id(id)
//	            .password(pw)
//	            .nickname("로그인계정")
//	            .role(Member.Role.USER)
//	            .status(Member.Status.ACTIVE)
//	            .points(0)
//	            .build();
//
//	    MemberDetails details = MemberDetails.builder()
//	            .email("login@test.com")
//	            .phone("012345679")
//				.gender(MemberDetails.Gender.MAN)
//				.address("광주광역시")
//				.postalCode("12345")
//				.birthDate(LocalDate.of(2025, 5, 5))
//				.build();
//
//	    mService.regist(MemberRegistRequest.builder().member(member).memberDetails(details).build());
//
//	    // when - 성공
//	    Member success = mService.login(id, pw);
//	    assertThat(success).isNotNull();
//
//	    // when - 실패 (잘못된 비밀번호)
//	    Member fail = mService.login(id, "wrongpass");
//	    assertThat(fail).isNull();
//	}
//
//	@Test
//	void logout_sets_token_cookie_null() {
//	    MockHttpServletResponse response = new MockHttpServletResponse();
//
//	    // when
//	    ResponseEntity<?> result = authController.logout(response);
//
//	    // then
//	    Cookie[] cookies = response.getCookies();
//	    assertThat(cookies).isNotEmpty();
//
//	    Optional<Cookie> tokenCookie = Arrays.stream(cookies)
//	        .filter(c -> c.getName().equals("token"))
//	        .findFirst();
//
//	    assertThat(tokenCookie).isPresent();
//	    assertThat(tokenCookie.get().getValue()).isNull();
//	    assertThat(tokenCookie.get().getMaxAge()).isEqualTo(0); // 삭제 확인
//	}
//	
//	
//	@Test
//	void checkLoginStatus_with_valid_token() {
//	    // given
//	    String id = "statusUser";
//	    Member member = makeMember(id);
//	    mService.regist(new MemberRegistRequest(member, makeDetails()));
//
//	    String token = jwtTokenProvider.createToken(member.getMno(), member.getId(), member.getNickname(), member.getRole());
//	    MockHttpServletRequest request = new MockHttpServletRequest();
//	    request.setCookies(new Cookie("token", token));
//
//	    // Spring Security Context 세팅이 필요한 경우 별도 설정 필요
//	    SecurityContextHolder.getContext().setAuthentication(
//	        jwtTokenProvider.getAuthentication(token)
//	    );
//
//	    // when
//	    ResponseEntity<?> response = authController.checkLoginStatus();
//
//	    // then
//	    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//	    assertThat(response.getBody()).asString().contains("로그인됨");
//	}
//
//}
