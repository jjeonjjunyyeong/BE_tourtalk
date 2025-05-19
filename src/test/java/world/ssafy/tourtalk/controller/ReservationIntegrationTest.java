package world.ssafy.tourtalk.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import world.ssafy.tourtalk.model.dto.enums.ReservationStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.ReservationRequest;
import world.ssafy.tourtalk.model.dto.response.ReservationResponse;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;
import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;

@SpringBootTest(properties = {
		  "jwt.secret=this_is_a_very_secure_and_long_enough_secret_key_for_hs256"
		}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper()
	        .registerModule(new JavaTimeModule());

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
	
	@BeforeAll
    void setup() {
        int testMno = 100; // 테스트용 mno
        String userId = "curatorUser";
        String nickname = "test";
        Role role = Role.USER;
        
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
    void 예약_등록_성공() throws Exception {
        ReservationRequest request = ReservationRequest.builder()
            .productId(1)
            .participantCount(2)
            .totalPrice(10000)
            .paymentMethod("카드")
            .build();

        mockMvc.perform(post("/api/v1/reservation")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    void 내_예약_목록_조회() throws Exception {
        // 미리 예약 등록
        예약_등록_성공(); // 위 메서드 직접 호출하거나 공통 유틸 함수로 분리해도 됨

        MvcResult result = mockMvc.perform(get("/api/v1/reservation")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<ReservationResponse> responseList = objectMapper.readValue(
            responseJson, new TypeReference<>() {}
        );

        assertThat(responseList).isNotEmpty();
        assertThat(responseList.get(0).getReservationStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PAYMENT);
    }

    @Test
    void 예약_취소_성공() throws Exception {
        // 예약 등록 → 예약 ID 획득
        예약_등록_성공();

        MvcResult result = mockMvc.perform(get("/api/v1/reservation")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        List<ReservationResponse> responseList = objectMapper.readValue(
            responseJson, new TypeReference<>() {}
        );

        int reservationId = responseList.get(0).getReservationId();

        // 예약 취소 요청
        mockMvc.perform(put("/api/v1/reservation/" + reservationId + "/cancel")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        // 재조회 시 상태 확인
        MvcResult afterCancel = mockMvc.perform(get("/api/v1/reservation")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn();

        List<ReservationResponse> afterCancelList = objectMapper.readValue(
            afterCancel.getResponse().getContentAsString(),
            new TypeReference<>() {}
        );

        assertThat(afterCancelList.get(0).getReservationStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    
}