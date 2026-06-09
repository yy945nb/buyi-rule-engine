package com.ymware.engine.domain.ai.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single message in a chat conversation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmMessage {

    public enum Role {
        SYSTEM, USER, ASSISTANT, TOOL
    }

    private Role role;
    private String content;

    public static LlmMessage system(String content) {
        return new LlmMessage(Role.SYSTEM, content);
    }

    public static LlmMessage user(String content) {
        return new LlmMessage(Role.USER, content);
    }

    public static LlmMessage assistant(String content) {
        return new LlmMessage(Role.ASSISTANT, content);
    }
}
