package dev.nhairlahovic.employeeservice.config;

import lombok.SneakyThrows;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.username}")
    private String elasticsearchUsername;

    @Value("${elasticsearch.password}")
    private String elasticsearchPassword;

    @Value("${elasticsearch.ssl.insecure}")
    private Boolean sslInsecure ;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchHost)
                .usingSsl(sslInsecure ? getInsecureSSLContext() : getSecureSSLContext())
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .withSocketTimeout(30000)
                .build();
    }

    // Creates an SSLContext that trusts all certificates, useful for development or testing (not secure for production).
    @SneakyThrows
    private SSLContext getInsecureSSLContext() {
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
