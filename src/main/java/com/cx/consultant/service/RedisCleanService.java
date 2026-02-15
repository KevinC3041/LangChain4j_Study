package com.cx.consultant.service;

public interface RedisCleanService {

    public long deleteByPrefix(String prefix);

}
