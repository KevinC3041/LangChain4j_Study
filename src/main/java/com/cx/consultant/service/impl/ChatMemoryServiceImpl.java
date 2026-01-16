package com.cx.consultant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.consultant.mapper.ChatMemoryMapper;
import com.cx.consultant.service.ChatMemoryService;
import entity.ChatMemoryEntity;
import org.springframework.stereotype.Service;

@Service
public class ChatMemoryServiceImpl extends ServiceImpl<ChatMemoryMapper, ChatMemoryEntity> implements ChatMemoryService {

}
