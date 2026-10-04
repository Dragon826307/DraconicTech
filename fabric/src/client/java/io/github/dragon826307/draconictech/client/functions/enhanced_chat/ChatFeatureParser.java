package io.github.dragon826307.draconictech.client.functions.enhanced_chat;

public interface ChatFeatureParser {
    EnhancedChatParseResult parse(String raw_string, String[] args);
}
