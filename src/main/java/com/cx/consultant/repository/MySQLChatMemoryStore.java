package com.cx.consultant.repository;

import com.cx.consultant.mapper.ChatMemoryMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import entity.ChatMemoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class MySQLChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private ChatMemoryMapper mapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        ChatMemoryEntity entity = mapper.selectById(memoryId.toString());
        if (entity == null || entity.getContent() == null) {
            return List.of();
        }
        List<ChatMessage> list = ChatMessageDeserializer.messagesFromJson(entity.getContent());
        return list;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        String json = ChatMessageSerializer.messagesToJson(list);

        ChatMemoryEntity entity = new ChatMemoryEntity();
        entity.setMemoryId(memoryId.toString());
        entity.setContent(json);
        entity.setUpdatedTime(LocalDateTime.now());

        mapper.insertOrUpdate(entity);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        mapper.deleteById(memoryId.toString());
    }
}
