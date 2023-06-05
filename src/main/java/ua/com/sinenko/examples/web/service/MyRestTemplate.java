package ua.com.sinenko.examples.web.service;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * RestTemplate with authorization and ignoring certificate verification
 * */
@Service
public class MyRestTemplate {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate;

    private String userName = "userName";
    private String userPassword = "password";
    private String endpoint = "https://localhost:7090/service/json/";

    public MyRestTemplate() {
    }

    @PostConstruct
    public void init() {
        SSLContext sslContext = createIgnoreSSLContext();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                    ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                }
                super.prepareConnection(connection, httpMethod);
            }
        };

        this.restTemplate = new RestTemplate(requestFactory);
    }

    public ResponseEntity<String> getResource(Long resourceId) {
        ResponseEntity<String> result = restTemplate.exchange(endpoint + resourceId, HttpMethod.GET, new HttpEntity<Void>(createHeaders(userName, userPassword)), String.class);
        return result;
    }


    private SSLContext createIgnoreSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{createIgnoreCertTrustManager()}, null);
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSL context", e);
        }
    }

    private X509TrustManager createIgnoreCertTrustManager() {
        return new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private HttpHeaders createHeaders(String username, String password){
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode( auth.getBytes());
        String authHeader = "Basic " + new String( encodedAuth );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", authHeader);
        return httpHeaders;
    }
}