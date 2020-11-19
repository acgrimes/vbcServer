package com.dd.vbc.messageService.httpClient;

import com.dd.vbc.Application;
import com.dd.vbc.domain.Server;
import com.dd.vbc.enums.Request;
import com.dd.vbc.enums.ServerConsensusState;
import com.dd.vbc.messageService.request.LeaderNoticeRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.logging.Logger;

@SpringBootTest(classes={Application.class, HttpMessage.class, HttpTestController.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HttpMessageTest {

    private static final Logger log = Logger.getLogger(HttpMessageTest.class.getSimpleName());

    @LocalServerPort
    private int port = 8444;

//    private TomcatServletWebServerFactory tomcatServletWebServerFactory;
//
//    @Autowired
//    public void setTomcatServletWebServerFactory(TomcatServletWebServerFactory tomcatServletWebServerFactory) {
//        this.tomcatServletWebServerFactory = tomcatServletWebServerFactory;
//        tomcatServletWebServerFactory.setPort(port);
//        tomcatServletWebServerFactory.setContextPath("");
//    }

    private HttpTestController httpTestController;

    @Autowired
    public void setTestProxyController(HttpTestController httpTestController) {
        this.httpTestController = httpTestController;
    }

    private HttpMessage httpMessage;

    @Autowired
    public void setHttpMessage(HttpMessage httpMessage) {
        this.httpMessage = httpMessage;
        this.httpMessage.setHost("localhost");
        this.httpMessage.setPort(port);
    }

    @Test
    public void postMessageTest() {

        Server server = new Server("A", "localhost", 8444, 61005, ServerConsensusState.Leader);
        LeaderNoticeRequest leaderNoticeRequest = new LeaderNoticeRequest(Request.LeaderNotification, server);
        GeneralResponse generalResponse = httpMessage.postMessage(leaderNoticeRequest);
        log.info(generalResponse.toString());
    }
}
