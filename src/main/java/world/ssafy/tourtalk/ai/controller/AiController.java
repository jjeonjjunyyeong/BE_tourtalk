package world.ssafy.tourtalk.ai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.ai.service.AiService;
import world.ssafy.tourtalk.controller.RestControllerHelper;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController implements RestControllerHelper{
    private final AiService chatService;

    @PostMapping("/simple")
    ResponseEntity<?> simpleGeneration(@RequestBody Map<String, String> userInput) {
        try {
            Object result = chatService.simpleGeneration(userInput.get("message"));
            
            return handleSuccess(Map.of("message", result));
        } catch (Exception e) {
            log.error("Simple AI 호출 실패: ", e);
            return handleFail(new RuntimeException("현재 AI 기능을 사용할 수 없습니다."));
        }
    }

    @PostMapping("/advised")
    ResponseEntity<?> advisedGeneration(@RequestBody Map<String, String> userInput) {
        try {
            Object result = chatService.advisedGeneration(userInput.get("message"));
            return handleSuccess(Map.of("message", result));
        } catch (Exception e) {
            log.error("Advised AI 호출 실패: ", e);
            return handleFail(new RuntimeException("현재 AI 기능을 사용할 수 없습니다."));
        }
    }

    @PostMapping("/chat")
    ResponseEntity<?> travelChatGeneration(@RequestBody Map<String, String> userInput) {
        try {
            String result = chatService.travelChatGeneration(userInput.get("message"));
            System.out.println(result.toString());
            return handleSuccess(Map.of("message", result));
        } catch (Exception e) {
            log.error("Travel chat AI 호출 실패: ", e);
            return handleFail(new RuntimeException("현재 AI 기능을 사용할 수 없습니다."));
        }
    }

    @PostMapping("/attraction-info")
    ResponseEntity<?> attractionInfoGeneration(@RequestBody Map<String, String> request) {
        try {
            String attractionName = request.get("attractionName");
            String address = request.get("address");
            String overview = request.get("overview");

            if (attractionName == null || attractionName.trim().isEmpty()) {
                return handleFail(new IllegalArgumentException("관광지 이름이 필요합니다."));
            }

            String result = chatService.attractionInfoGeneration(attractionName, address, overview);
            System.out.println(result);
            return handleSuccess(Map.of("message", result));
        } catch (Exception e) {
            log.error("Attraction info AI 호출 실패: ", e);
            return handleFail(new RuntimeException("현재 AI 기능을 사용할 수 없습니다."));
        }
    }
}