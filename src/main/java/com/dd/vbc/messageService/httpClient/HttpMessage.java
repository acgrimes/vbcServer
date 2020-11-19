package com.dd.vbc.messageService.httpClient;

import com.dd.vbc.enums.ReturnCode;
import com.dd.vbc.messageService.request.LeaderNoticeRequest;
import com.dd.vbc.messageService.response.GeneralResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;

@Component
public class HttpMessage {

    private static final Logger log = Logger.getLogger(HttpMessage.class.getSimpleName());

    private String host = "localhost";
    private int port = 8443;

    public HttpMessage() {}
    public HttpMessage(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public GeneralResponse postMessage(LeaderNoticeRequest leaderNoticeRequest) {

        GeneralResponse generalResponse = null;
        try {
            TlsContextBuilder tlsContextBuilder = new TlsContextBuilder();
            log.info("http://"+host+":"+port+"/leaderNotification");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://"+host+":"+port+"/leaderNotification"))
                    .header("Content-Type", "application/octal-stream")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(SerializationUtils.serialize(leaderNoticeRequest)))
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
//                    .sslContext(tlsContextBuilder.build())
                    .authenticator(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    "user",
                                    "password".toCharArray());
                        }
                    })
                    .connectTimeout(Duration.ofSeconds(1))
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            log.info(String.valueOf(response.statusCode()));
            if(response.statusCode()==200) {
                generalResponse = SerializationUtils.deserialize(response.body());
            } else {
                generalResponse = new GeneralResponse(null, ReturnCode.FAILURE, null);
            }

        } catch(IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return generalResponse;
    }
}
