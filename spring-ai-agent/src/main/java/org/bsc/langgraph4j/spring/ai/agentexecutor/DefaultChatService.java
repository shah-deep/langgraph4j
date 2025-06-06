package org.bsc.langgraph4j.spring.ai.agentexecutor;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

class DefaultChatService implements ChatService {
    final ChatClient chatClient;
    final List<ToolCallback> tools;
    final boolean streaming;

    public DefaultChatService(AgentExecutor.Builder builder ) {
        Objects.requireNonNull(builder.chatModel,"chatModel cannot be null!");
        this.tools = builder.tools;
        this.streaming = builder.streaming;
        var toolOptions = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false) // Disable automatic tool execution
                .build();

        chatClient = ChatClient.builder(builder.chatModel)
                .defaultOptions(toolOptions)
                .defaultToolCallbacks( builder.tools )
                .defaultSystem( builder.systemMessage != null ?
                        builder.systemMessage :
                        "You are a helpful AI Assistant answering questions." )
                .build();
    }

    @Override
    public ChatResponse execute(List<Message> messages) {
        return chatClient
                .prompt()
                .messages( messages )
                .call()
                .chatResponse();
    }

    @Override
    public Flux<ChatResponse> streamingExecute(List<Message> messages) {
        return chatClient
                .prompt()
                .messages( messages )
                .stream()
                .chatResponse();
    }

    @Override
    public List<ToolCallback> tools() {
        return List.copyOf(tools);
    }

}
