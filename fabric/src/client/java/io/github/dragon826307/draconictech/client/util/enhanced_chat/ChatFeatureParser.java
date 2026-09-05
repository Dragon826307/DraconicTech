package io.github.dragon826307.draconictech.client.util.enhanced_chat;

public interface ChatFeatureParser {
    EnhancedChatParseResult parse(String raw_string, String[] args);
}
