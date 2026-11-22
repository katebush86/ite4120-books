package ee.taltech.ite4120.books.borrowerregistry;

import ee.taltech.ite4120.books.borrowerregistry.http.HttpBorrowerRegistryAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Chooses the transport behind {@link BorrowerRegistryAdapter}: plain HTTPS+JSON
 * against {@code books.borrower-registry.url}. Locally that URL points at the
 * course's built-in mock population registry ({@code /mock-registry}), the same
 * imitated environment the animals example uses.
 */
@Configuration
public class BorrowerRegistryConfig {

    @Bean
    public BorrowerRegistryAdapter borrowerRegistryAdapter(
            @Value("${books.borrower-registry.url:http://localhost:18440/mock-registry}") String url) {
        return new HttpBorrowerRegistryAdapter(url);
    }
}
