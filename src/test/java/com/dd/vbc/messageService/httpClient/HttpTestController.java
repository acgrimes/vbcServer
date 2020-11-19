package com.dd.vbc.messageService.httpClient;

import com.dd.vbc.domain.Voter;
import com.dd.vbc.enums.Response;
import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.LeaderNoticeRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class HttpTestController {

    private static final Logger log = Logger.getLogger(HttpTestController.class.getSimpleName());

    @PostMapping(value = "/leaderNotification", consumes = "application/octal-stream", produces = "application/octal-stream")
    public byte[] postLeaderNotification(@RequestBody byte[] request) {
        LeaderNoticeRequest leaderNoticeRequest = SerializationUtils.deserialize(request);
        log.info(leaderNoticeRequest.toString());
        Voter voter = new Voter();
        GeneralResponse generalResponse = new GeneralResponse(voter, ReturnCode.SUCCESS, Response.LeaderRequestAccepted);
        return SerializationUtils.serialize(generalResponse);
    }
}