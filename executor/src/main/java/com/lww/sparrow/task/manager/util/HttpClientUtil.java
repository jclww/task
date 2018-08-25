package com.lww.sparrow.task.manager.util;

import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public class HttpClientUtil {

    private static final int CONNECT_TIMEOUT = 3000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 3000;
    private static final int SOCKET_TIMEOUT = 5000;
    public static final int OK = 200;

    @Data
    public static class Response {
        private int status;
        private String body;
    }

    public static Response request(String url, Map<String, String> headers) throws Exception {
        HttpRequestBase req = new HttpGet(url);

        req.setConfig(buildRequestConfig());
        if (headers != null) {
            headers.forEach(req::setHeader);
        }
        CloseableHttpClient client = null;
        try {
            client = HttpClientBuilder.create().build();
            // 发送请求，并获取返回值
            HttpResponse resp = client.execute(req);
            HttpEntity httpEntity = resp.getEntity();
            String respBody = (httpEntity == null) ? null : EntityUtils.toString(httpEntity, "UTF-8");
            Response ret = new Response();
            ret.body = respBody;
            ret.status = resp.getStatusLine().getStatusCode();
            return ret;
        } finally {
            release(req, client);
        }
    }
    /**
     * 释放资源
     *
     * @param request
     * @param httpClient
     */
    private static void release(HttpRequestBase request, CloseableHttpClient httpClient) {
        if (request != null) {
            request.abort();
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
            }
        }
    }

    private static RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
    }
}
