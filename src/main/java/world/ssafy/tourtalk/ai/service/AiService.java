package world.ssafy.tourtalk.ai.service;

public interface AiService {
    public default Object simpleGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    };

    public default String reReadingGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    };

    public default String advisedGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    }

    // 여행 관련 채팅 AI
    public default String travelChatGeneration(String userInput) {
        throw new RuntimeException("not yet ready");
    };

    // 관광지 정보 기반 AI 설명
    public default String attractionInfoGeneration(String attractionName, String address, String overview) {
        throw new RuntimeException("not yet ready");
    };
}