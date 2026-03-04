package com.cx.consultant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.consultant.mapper.CaseProgressMapper;
import com.cx.consultant.service.CaseProgressService;
import entity.CaseProgressEntity;
import org.springframework.stereotype.Service;

@Service
public class CaseProgressServiceImpl extends ServiceImpl<CaseProgressMapper, CaseProgressEntity> implements CaseProgressService {

}
