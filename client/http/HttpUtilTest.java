package http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtilTest.class);

    private static  volatile CloseableHttpClient localHttpClient;

    /**
     * 默认超时时间:10分钟，获取连接的timeout和获取socket数据的timeout都是10分钟
     */
    private static int timeOutMilliSecond = 60000;

    private static String charset = "utf-8";

    private static final int MAX_TOTAL = 1000;

    /**
     * 连接池链接耗尽等待时间
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 2000;

    private static final int MAX_PER_ROUTE = 10;

    private static final Object SYNC_LOCK = new Object();

    private static HttpClientBuilder httpBuilder;

    static {
        // 初始化连接池管理器，设置http的状态参数
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager);
    }

    /**
     * 获取连接
     *
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        if (localHttpClient == null) {
            synchronized (SYNC_LOCK) {
                localHttpClient = createHttpClient();
            }
        }
        return localHttpClient;
    }

    /**
     * 创建带有超时时间的连接
     *
     * @param invokeTimeout 超时时间
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient createHttpClient(int invokeTimeout) {
        if (invokeTimeout > 0 && invokeTimeout < 60 * 30 * 1000) {
            timeOutMilliSecond = invokeTimeout;
        }
        Builder builder = RequestConfig.custom().setSocketTimeout(timeOutMilliSecond).setConnectTimeout(timeOutMilliSecond).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        RequestConfig defaultRequestConfig = builder.build();

        HttpClientBuilder httpClientBuilder = httpBuilder.setDefaultRequestConfig(defaultRequestConfig);

        // 添加了检测系统级代理
        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient;
    }

    /**
     * 获取带有超时的连接
     *
     * @param invokeTimeout 超时时间
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient getHttpClient(int invokeTimeout) {
        synchronized (SYNC_LOCK) {
            localHttpClient = createHttpClient(invokeTimeout);
        }
        return localHttpClient;
    }

    /**
     * 创建连接
     *
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient createHttpClient() {
        Builder builder = RequestConfig.custom().setSocketTimeout(timeOutMilliSecond).setConnectTimeout(timeOutMilliSecond);
        RequestConfig defaultRequestConfig = builder.build();
        HttpClientBuilder httpClientBuilder = httpBuilder.setDefaultRequestConfig(defaultRequestConfig);


        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient;
    }

    /**
     * POST方式调用-参数MAP
     *
     * @param url url
     * @param paramMap 参数map
     * @return string
     * @throws Exception
     */
    public static String post(String url, Map<String, String> paramMap, Integer timeout) throws Exception {
        CloseableHttpClient httpClient = getHttpClient();
        logger.info("[HttpUtils-post-request:]url:{},paramMap:{}", url, paramMap);
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramsPair = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            paramsPair.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        initConfig(httpPost, timeout);
        httpPost.setEntity(new UrlEncodedFormEntity(paramsPair, charset));
        String response = handleResponseToString(httpClient, httpPost);
        logger.info("[HttpUtils-post-response2:]url:{},paramMap:{},response:{}", url, paramMap, response);
        return response;
    }

    /**
     * 处理POST的返回结果
     *
     * @param httpClient httpClient
     * @param httpPost post
     * @return string
     * @throws Exception
     */
    private static String handleResponseToString(CloseableHttpClient httpClient, HttpPost httpPost) throws Exception {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, charset);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    public static void initConfig(HttpPost httpPost, Integer timeout) {
        if (null == timeout || timeout <= 0) {
            timeout = timeOutMilliSecond;
        }
        Builder builder = RequestConfig.custom();
        builder.setSocketTimeout(timeout).setConnectTimeout(timeout).setConnectionRequestTimeout(timeout);// 设置请求和传输超时时间
        RequestConfig requestConfig = builder.build();
        httpPost.setConfig(requestConfig);
    }

    public static void main(String[] args) {
        String url = "反欺诈服务接口地址";
        //业务入參
        Map<String,String> map = new HashMap<String, String>();
        map.put("appName","mobile");
        map.put("eventId","mobile_test");
        map.put("invokeType","10");
        map.put("transId","123456");
        //其他请求入參。。。
        try {
            String response = HttpUtilTest.post(url,map,2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}