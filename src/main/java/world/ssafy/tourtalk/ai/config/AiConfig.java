package world.ssafy.tourtalk.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.ai.advisor.ReReadingAdvisor;

@Configuration
@Slf4j
public class AiConfig {
	@Value("${ssafy.ai.system-prompt}")
    String systemPrompt;
	@Value("${ssafy.ai.travel-chat-prompt}")
    String travelChatPrompt;
    
    @Value("${ssafy.ai.attraction-info-prompt}")
    String attractionInfoPrompt;
    
    @Bean
    ChatClient simpleChatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .build();
    }

    @Bean
    ChatClient reReadingChatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1), new ReReadingAdvisor(0))
                .build();
    }

    @Bean
    ChatMemory chatMemory() {
        return new InMemoryChatMemory(); // 메모리에 저장하기 위한 ChatMemory
    }

    @Bean
    ChatClient advisedChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder.defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                //.defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory))
                .build();
    }
    
    @Bean
    ChatClient travelChatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(travelChatPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .build();
    }
    
    @Bean
    ChatClient attractionInfoClient(ChatClient.Builder builder) {
        return builder.defaultSystem(attractionInfoPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1))
                .build();
    }
}
