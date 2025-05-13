package world.ssafy.tourtalk.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import world.ssafy.tourtalk.model.dto.Curator;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;
import world.ssafy.tourtalk.model.dto.request.MemberRegistRequest;
import world.ssafy.tourtalk.model.dto.request.MemberUpdateRequest;
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
	
	private Member makeMember(String id) {
	    return Member.builder()
	        .id(id).password("1234").nickname("test")
	        .role(Member.Role.USER).status(Member.Status.ACTIVE)
	        .points(0)
	        .build();
	}

	private MemberDetails makeDetails() {
	    return MemberDetails.builder()
	        .email("test@user.com").phone("01012348756")
	        .gender(MemberDetails.Gender.MAN)
			.address("광주광역시")
			.postalCode("12345")
			.birthDate(LocalDate.of(2025, 5, 5))
	        .build();
	}

	
	@Test
	void regist() {
		Member member = Member.builder()
				.id("TestUser")
				.password("1234")
				.nickname("테스트")
				.role(Member.Role.USER)
				.status(Member.Status.ACTIVE)
				.points(0)
				.build();
		
		MemberDetails details = MemberDetails.builder()
				.email("test@test.com")
				.phone("01012345678")
				.gender(MemberDetails.Gender.MAN)
				.address("광주광역시")
				.postalCode("12345")
				.birthDate(LocalDate.of(2025, 5, 5))
				.build();
		
		MemberRegistRequest request = MemberRegistRequest.builder()
				.member(member)
				.memberDetails(details)
				.build();
		
		int result = mService.regist(request);
		assertThat(result).isEqualTo(2);
	}
	
	@Test
	void regist_with_curator() {
	    // 1. Member 생성 (큐레이터 역할)
	    Member member = Member.builder()
	        .id("CuratorUser")
	        .password("1234")
	        .nickname("큐레이터")
	        .role(Member.Role.CURATOR) // 꼭 CURATOR로 설정해야 curator insert 됨
	        .status(Member.Status.ACTIVE)
	        .points(0)
	        .build();

	    // 2. MemberDetails 생성
	    MemberDetails details = MemberDetails.builder()
	        .email("curator@test.com")
	        .phone("01098765432")
	        .gender(MemberDetails.Gender.WOMAN)
	        .address("서울특별시 강남구")
	        .postalCode("12345")
	        .birthDate(LocalDate.of(1990, 1, 1))
	        .build();

	    // 3. Curator 추가 정보
	    Curator curator = Curator.builder()
	        .curatorNo("C-2025-001")
	        .curatorImg("profile_img.png")
	        .build();

	    // 4. 요청 객체 조립
	    MemberRegistRequest request = MemberRegistRequest.builder()
	        .member(member)
	        .memberDetails(details)
	        .curator(curator)
	        .build();

	    // 5. 서비스 호출 및 검증
	    int result = mService.regist(request);
	    assertThat(result).isEqualTo(3); // Member + MemberDetails + Curator = 3건
	}

	
	@Test
	void login_success_and_failure() {
	    // given
	    String id = "TestUser2";
	    String pw = "1234";
	    Member member = Member.builder()
	            .id(id)
	            .password(pw)
	            .nickname("로그인계정")
	            .role(Member.Role.USER)
	            .status(Member.Status.ACTIVE)
	            .points(0)
	            .build();

	    MemberDetails details = MemberDetails.builder()
	            .email("login@test.com")
	            .phone("012345679")
				.gender(MemberDetails.Gender.MAN)
				.address("광주광역시")
				.postalCode("12345")
				.birthDate(LocalDate.of(2025, 5, 5))
				.build();

	    mService.regist(MemberRegistRequest.builder().member(member).memberDetails(details).build());

	    // when - 성공
	    Member success = mService.login(id, pw);
	    assertThat(success).isNotNull();

	    // when - 실패 (잘못된 비밀번호)
	    Member fail = mService.login(id, "wrongpass");
	    assertThat(fail).isNull();
	}

	@Test
	void logout_sets_token_cookie_null() {
	    MockHttpServletResponse response = new MockHttpServletResponse();

	    // when
	    ResponseEntity<?> result = memberController.logout(response);

	    // then
	    Cookie[] cookies = response.getCookies();
	    assertThat(cookies).isNotEmpty();

	    Optional<Cookie> tokenCookie = Arrays.stream(cookies)
	        .filter(c -> c.getName().equals("token"))
	        .findFirst();

	    assertThat(tokenCookie).isPresent();
	    assertThat(tokenCookie.get().getValue()).isNull();
	    assertThat(tokenCookie.get().getMaxAge()).isEqualTo(0); // 삭제 확인
	}

	@Test
	void me_returns_member_response() {
	    String id = "infoUser";
	    Member member = makeMember(id); // 직접 만든 회원
	    mService.regist(new MemberRegistRequest(member, makeDetails()));

	    MemberResponse result = mService.me(id);
	    assertThat(result.getMember().getId()).isEqualTo(id);
	    assertThat(result.getMemberDetails()).isNotNull();
	}
	
	@Test
	void me_from_token_cookie() {
	    String id = "tokenUser";
	    Member member = makeMember(id);
	    mService.regist(new MemberRegistRequest(member, makeDetails()));
	    String token = jwtTokenProvider.createToken(id, member.getRole());

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.setCookies(new Cookie("token", token));

	    ResponseEntity<?> response = memberController.me(request);

	    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	    MemberResponse body = (MemberResponse) response.getBody();
	    assertThat(body.getMember().getId()).isEqualTo(id);
	}


	@Test
	void update_member_info() {
	    String id = "updateUser";
	    mService.regist(new MemberRegistRequest(makeMember(id), makeDetails()));

	    MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
	        .member(Member.builder().id(id).nickname("변경된닉").build())
	        .memberDetails(MemberDetails.builder().email("test@user.com").phone("12345678900").build())
	        .build();

	    int result = mService.update(updateRequest);
	    assertThat(result).isGreaterThanOrEqualTo(1);

	    MemberResponse after = mService.me(id);
	    assertThat(after.getMember().getNickname()).isEqualTo("변경된닉");
	    assertThat(after.getMemberDetails().getEmail()).isEqualTo("test@user.com");
	}

	
	@Test
	void delete_member_sets_status_deleted() {
	    String id = "deleteUser";
	    mService.regist(new MemberRegistRequest(makeMember(id), makeDetails()));

	    int result = mService.delete(id);
	    assertThat(result).isEqualTo(1);

	    Member deleted = mService.login(id, "1234");
	    assertThat(deleted).isNull(); // status = DELETED라 로그인 안됨

	    MemberResponse after = mService.me(id);
	    assertThat(after.getMember().getStatus()).isEqualTo(Member.Status.DELETED);
	}

}
