package world.ssafy.tourtalk.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAiService implements AiService {

    private final ChatModel model2;

    @Qualifier("simpleChatClient")
    private final ChatClient simpleChatClient;

    public Object simpleGeneration(String userInput) {
        return simpleChatClient.prompt()// fluent api 형태로 user, system 등 prompt 파트 구성
                .system(t -> t.param("language", "korean"))
                .user(userInput)// // user 메시지 전달
                .call(); // 실제 모델 호출
    }

    @Qualifier("reReadingChatClient")
    private final ChatClient reReadingChatClient;

    @Override
    public String reReadingGeneration(String userInput) {
        return this.reReadingChatClient.prompt() // fluent api 형태로 user, system 등 prompt 파트 구성
                .system(c -> c.param("language", "Korean").param("character", "귀여운"))
                .user(userInput)// user 메시지 전달
                .call() // model 호출
                .content(); // 응답의 내용을 단순한 문자열로 반환
    }

    @Qualifier("advisedChatClient")
    private final ChatClient advisedChatClient;

    @Override
    public String advisedGeneration(String userInput) {
        return this.advisedChatClient.prompt() // fluent api 형태로 user, system 등 prompt 파트 구성
                .system(c -> c.param("language", "Korean").param("character", "Chill한")).user(userInput)// user 메시지 전달
                .call() // model 호출
                .content(); // 응답의 내용을 단순한 문자열로 반환
    }

    @Qualifier("travelChatClient")
    private final ChatClient travelChatClient;

    @Override
    public String travelChatGeneration(String userInput) {
        try {
            return this.travelChatClient.prompt()
                    .user(userInput)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Travel chat AI 호출 실패: ", e);
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.");
        }
    }

    @Qualifier("attractionInfoClient")
    private final ChatClient attractionInfoClient;

    @Override
    public String attractionInfoGeneration(String attractionName, String address, String overview) {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("관광지 정보:\n");
            prompt.append("- 이름: ").append(attractionName).append("\n");
            prompt.append("- 주소: ").append(address).append("\n");

            if (overview != null && !overview.trim().isEmpty()) {
                prompt.append("- 상세 설명: ").append(overview).append("\n");
            }

            prompt.append("\n위 정보를 바탕으로 이 관광지에 대한 매력적인 설명을 5줄 이내로 작성해주세요.");

            return this.attractionInfoClient.prompt()
                    .user(prompt.toString())
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Attraction info AI 호출 실패: ", e);
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.");
        }
    }
}