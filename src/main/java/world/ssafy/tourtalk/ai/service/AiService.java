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
}
