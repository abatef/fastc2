    package com.abatef.fastc2.config;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.elasticsearch.client.ClientConfiguration;
    import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
    import org.springframework.data.elasticsearch.support.HttpHeaders;

    @Configuration
    public class ElasticsearchConfig extends ElasticsearchConfiguration {

        @Value("${elastic-search-conn-str}")
        private String connectionString;

        @Value("${elastic-search-api-key}")
        private String ApiKey;

        @Override
        public ClientConfiguration clientConfiguration() {
            return ClientConfiguration.builder()
                    .connectedTo(connectionString)
                    .usingSsl()
                    .withHeaders(() -> {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add("Authorization", "ApiKey " +  ApiKey);
                        return headers;
                    }).build();
        }
    }
