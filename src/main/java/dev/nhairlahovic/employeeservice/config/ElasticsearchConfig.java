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
        String vcapServices = System.getenv("VCAP_SERVICES");

        if (vcapServices == null || vcapServices.isEmpty()) {
            log.info("VCAP_SERVICES not found");
            throw new RuntimeException("VCAP_SERVICES not found");
        }

        try {
            log.info("VCAP_SERVICES found: {}", vcapServices);

            // Parse the VCAP_SERVICES environment variable to extract the credentials
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(vcapServices);
            JsonNode credentials = root.path(serviceOfferingName).get(0).path("credentials");

            String url = credentials.get("uri").asText();
            String hostAndPort = extractHostAndPortFromUri(url);
            String username = credentials.get("username").asText();
            String password = credentials.get("password").asText();

            ClientConfiguration.MaybeSecureClientConfigurationBuilder configBuilder = ClientConfiguration.builder()
                    .connectedTo(hostAndPort);

            if (sslEnabled) {
                configBuilder.usingSsl(trustAllCertificates ? getTrustAllSSLContext() : getSecureSSLContext());
            }

            return configBuilder
                    .withBasicAuth(username, password)
                    .withSocketTimeout(30000)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse VCAP_SERVICES", e);
        }
    }

    public static String extractHostAndPortFromUri(String uriString) {
        try {
            URI uri = new URI(uriString);
            return uri.getHost() + ":" + uri.getPort();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
