package com.mateuszmedon.assignment.mapper;

import com.mateuszmedon.assignment.entity.Request;
import com.mateuszmedon.assignment.shared.dto.RequestDto;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RequestMapperTest {
    private static final String APPLICATION_LOG = "APPLICATION_LOG";
    private static final String ID = "123a";
    private static final String HOST_1 = "HOST1";
    private RequestMapper eventConverter = new RequestMapper();

    @Test
    public void testEventDTOToEvent_NonAlertEvent() {
        RequestDto start = new RequestDto(ID, RequestDto.State.STARTED, APPLICATION_LOG, HOST_1, 123L);
        RequestDto finish = new RequestDto(ID, RequestDto.State.FINISHED, APPLICATION_LOG, HOST_1, 124L);

        Request event = eventConverter.RequestDTOToRequest(start, finish);
        assertFalse("Request should not be returned as alert", event.isAlert());
    }

    @Test
    public void testEventDTOToEvent_AlertEvent() {
        RequestDto start = new RequestDto(ID, RequestDto.State.STARTED, APPLICATION_LOG, HOST_1, 123L);
        RequestDto finish = new RequestDto(ID, RequestDto.State.FINISHED, APPLICATION_LOG, HOST_1, 128L);

        Request event = eventConverter.RequestDTOToRequest(start, finish);
        assertTrue("Request should be returned as alert", event.isAlert());
    }
}