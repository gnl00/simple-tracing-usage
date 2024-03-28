package com.stu.interceptor;

import com.stu.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 拦截 RestTemplate 请求，在 header 中添加 traceId
        HttpHeaders headers = request.getHeaders();
        headers.add(TraceConstants.TRACE_ID, MDC.get(TraceConstants.TRACE_ID));
        return execution.execute(request, body);
    }
}
