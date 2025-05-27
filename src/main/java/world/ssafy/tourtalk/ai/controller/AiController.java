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
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController implements RestControllerHelper{
	private final AiService chatService;
	
	@PostMapping("/simple")
    ResponseEntity<?> simpleGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.simpleGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }

    @PostMapping("/advised")
    ResponseEntity<?> advisedGeneration(@RequestBody Map<String, String> userInput) {
        Object result = chatService.advisedGeneration(userInput.get("message"));
        return handleSuccess(Map.of("message", result));
    }
}
