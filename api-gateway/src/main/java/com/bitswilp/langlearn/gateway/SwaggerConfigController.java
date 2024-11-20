package com.bitswilp.langlearn.gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class SwaggerConfigController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/v3/api-docs/swagger-config")
    public Map<String, Object> swaggerConfig(ServerHttpRequest serverHttpRequest) throws URISyntaxException {
        URI uri = serverHttpRequest.getURI();
        String baseUrl = new URI(uri.getScheme(), uri.getAuthority(), null, null, null).toString();

        Map<String, Object> swaggerConfig = new LinkedHashMap<>();
        List<Map<String, String>> swaggerUrls = new LinkedList<>();

        // List of services to include for Swagger aggregation
        List<String> servicesToInclude = List.of("IDENTITY-SERVICE", "LANGLEARN", "ProfileManagement");

        discoveryClient.getServices().stream()
                .filter(servicesToInclude::contains) // Only include specified services
                .forEach(serviceName -> {
                    Map<String, String> swaggerUrl = new LinkedHashMap<>();
                    swaggerUrl.put("name", serviceName);
                    swaggerUrl.put("url", baseUrl + "/" + serviceName.toLowerCase() + "/v3/api-docs");
                    swaggerUrls.add(swaggerUrl);
                });

        swaggerConfig.put("urls", swaggerUrls);
        return swaggerConfig;
    }
}
