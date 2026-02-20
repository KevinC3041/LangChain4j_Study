package com.cx.consultant.service;

import reactor.core.publisher.Flux;

public interface GameFlowService {

    public Flux<String> startGame(String memoryId);

}
