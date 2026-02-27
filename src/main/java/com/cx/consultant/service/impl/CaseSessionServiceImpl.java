package com.cx.consultant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.consultant.mapper.CaseSessionMapper;
import com.cx.consultant.service.CaseSessionService;
import entity.CaseSessionEntity;
import org.springframework.stereotype.Service;

@Service
public class CaseSessionServiceImpl extends ServiceImpl<CaseSessionMapper, CaseSessionEntity> implements CaseSessionService {

}
