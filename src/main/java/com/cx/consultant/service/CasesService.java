package com.cx.consultant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import entity.CasesEntity;

public interface CasesService extends IService<CasesEntity> {

    public CasesEntity randomSelectOne();

}
