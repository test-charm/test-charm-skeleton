package com.odde.atddv2;

import io.cucumber.java.Before;
import io.cucumber.java.zh_cn.并且;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ApiOrderSteps {
    @Value("${binstd-endpoint.key}")
    private String binstdAppKey;

    @Autowired
    private MockServerClient mockServerClient;

    @Before(order = 0)
    @SneakyThrows
    public void resetMockServer() {
        mockServerClient.reset();
    }

    @并且("存在快递单{string}的物流信息如下")
    public void 存在快递单的物流信息如下(String deliverNo, String json) {
        mockServerClient.when(request()
                        .withMethod("GET").withPath("/express/query")
                        .withQueryStringParameter("appkey", binstdAppKey)
                        .withQueryStringParameter("type", "auto")
                        .withQueryStringParameter("number", deliverNo), Times.unlimited())
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json));
    }
}
