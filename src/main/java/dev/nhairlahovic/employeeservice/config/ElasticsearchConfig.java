package dev.nhairlahovic.employeeservice.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Slf4j
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${service-offering.name}")
    private String serviceOfferingName;

    @Value("${elasticsearch.ssl.enabled}")
    private Boolean sslEnabled ;

    @Value("${elasticsearch.ssl.trustAllCertificates}")
    private Boolean trustAllCertificates ;

    @Override
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder configBuilder = ClientConfiguration.builder()
                .connectedTo(elasticsearchHost);

        if (sslEnabled) {
            configBuilder.usingSsl(trustAllCertificates ? getTrustAllSSLContext() : getSecureSSLContext());
        }

        return configBuilder
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .withSocketTimeout(30000)
                .build();
    }

    // Creates an SSLContext that trusts all certificates, useful for development or testing (not secure for production).
    @SneakyThrows
    private SSLContext getTrustAllSSLContext() {
        return SSLContexts.custom()
                .loadTrustMaterial((chain, authType) -> true) // Trust all certificates
                .build();
    }

    @SneakyThrows
    private SSLContext getSecureSSLContext() {
        // Initialize a KeyStore to hold trusted certificates
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null); // Initialize empty trust store

        // Load the X.509 certificate from a resource file
        Certificate cert;
        try (InputStream certificateInputStream = getResourceFileAsInputStream("cert.pem")) {
            cert = CertificateFactory.getInstance("X.509").generateCertificate(certificateInputStream);
        }

        // Add the certificate to the trust store with an alias
        trustStore.setCertificateEntry("elasticsearch", cert);

        // Create an SSLContext using the trust store
        return SSLContexts.custom()
                .loadTrustMaterial(trustStore, null)
                .build();
    }

    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = ElasticsearchConfig.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
