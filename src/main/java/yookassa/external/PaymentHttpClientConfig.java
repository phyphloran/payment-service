package yookassa.external;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import java.util.Base64;


@Configuration
public class PaymentHttpClientConfig {

    @Value("${yookassa.base-url}")
    private String paymentServiceBaseUrl;

    @Value("${yookassa.shop-id}")
    private String shopId;

    @Value("${yookassa.secret-key}")
    private String secretKey;

    @Bean
    RestClient paymentRestClient(RestClient.Builder builder) {
        String credentials = shopId + ":" + secretKey;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
        return builder
                .baseUrl(paymentServiceBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .build();
    }

    @Bean
    PaymentHttpClient paymentHttpClient(RestClient paymentRestClient) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(paymentRestClient))
                .build()
                .createClient(PaymentHttpClient.class);

    }

}
