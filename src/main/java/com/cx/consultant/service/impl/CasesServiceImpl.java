package com.cx.consultant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cx.consultant.mapper.CasesMapper;
import com.cx.consultant.service.CasesService;
import entity.CasesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CasesServiceImpl extends ServiceImpl<CasesMapper, CasesEntity> implements CasesService {

    @Autowired
    private CasesMapper casesMapper;

    public CasesEntity randomSelectOne() {
        return casesMapper.randomSelectOne();
    }

    public CasesEntity getCaseById(int id) {
        return casesMapper.testChosenOne(id);
    }


}
