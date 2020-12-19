package com.mateuszmedon.assignment.mapper;

import com.mateuszmedon.assignment.entity.Request;
import com.mateuszmedon.assignment.shared.dto.RequestDto;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {
    /**
     * Takes eventDTO start and finish objects and calculates (non)alert event based on elapsed timestamp
     *
     * @param startRequest
     * @param finishRequest
     * @return Request object
     */
    public Request RequestDTOToRequest(RequestDto startRequest, RequestDto finishRequest) {
        Long duration =  finishRequest.getTimestamp() - startRequest.getTimestamp();
        boolean isAlert = duration > 4;
        return new Request(startRequest.getId(), duration, startRequest.getType(), startRequest.getHost(), isAlert);
    }
}
