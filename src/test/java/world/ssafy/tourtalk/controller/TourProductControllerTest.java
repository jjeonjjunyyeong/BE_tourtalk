package world.ssafy.tourtalk.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import world.ssafy.tourtalk.model.dto.enums.PriceType;
import world.ssafy.tourtalk.model.dto.enums.ProductStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.service.TourProductService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;
import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest(properties = {
		  "jwt.secret=this_is_a_very_secure_and_long_enough_secret_key_for_hs256"
		}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TourProductControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private TourProductService tourProductService;

	private final ObjectMapper objectMapper = new ObjectMapper()
	        .registerModule(new JavaTimeModule());
	
	private String token;

	@BeforeAll
    void setup() {
        int testMno = 100; // 테스트용 mno
        String userId = "curatorUser";
        String nickname = "test";
        Role role = Role.CURATOR;
        
        CustomMemberPrincipal principal = new CustomMemberPrincipal(testMno, userId, nickname, role);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        token = "Bearer " + jwtTokenProvider.createToken(testMno, userId, nickname, role);
        System.out.println("사용할 JWT: Bearer " + token);
	}

	@AfterEach
	void clearContext() {
	    SecurityContextHolder.clearContext();
	}

	
	@Test
    void 상품_등록_성공() throws Exception {
        TourProductRequest request = TourProductRequest.builder()
                .locationNo(1)
                .title("테스트 상품")
                .description("설명입니다")
                .maxParticipants(20)
                .minParticipants(5)
                .priceType(PriceType.TOTAL)
                .price(50000)
                .startDate(LocalDate.now())
                .status(ProductStatus.OPEN)
                .thumbnailImg("test.jpg")
                .tags("문화,유적")
                .meetingPlace("광화문")
                .meetingTime(LocalTime.of(10, 0))
                .duration(90)
                .build();

        given(tourProductService.insert(any(), eq(100))).willReturn(true); // mno = 100

        mockMvc.perform(post("/api/v1/tourProduct")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("상품 등록 성공!"));
    }

	@Test
	void 상품_수정_성공() throws Exception {
	    TourProductRequest request = TourProductRequest.builder()
	            .title("수정된 제목")
	            .build();

	    given(tourProductService.update(eq(1), any(), eq(100))).willReturn(true); // mno = 100

	    mockMvc.perform(put("/api/v1/tourProduct/1")
	            .header("Authorization", token)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	        .andExpect(status().isOk())
	        .andExpect(content().string("상품 수정 성공!"));
	}

	@Test
	void 상품_소프트삭제_성공() throws Exception {
	    given(tourProductService.softDeleted(eq(1), eq(100))).willReturn(true); // mno = 100

	    mockMvc.perform(put("/api/v1/tourProduct/1/deleted")
	            .header("Authorization", token))
	        .andExpect(status().isOk())
	        .andExpect(content().string("상품이 삭제되었습니다."));
	}

}