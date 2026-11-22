package ee.taltech.ite4120.books.borrowerregistry.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.ite4120.books.borrowerregistry.BorrowerInfo;
import ee.taltech.ite4120.books.borrowerregistry.BorrowerRegistryAdapter;
import ee.taltech.ite4120.books.borrowerregistry.BorrowerRegistryException;
import ee.taltech.ite4120.books.borrowerregistry.http.wire.RrPersonResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Fetches a person from the population registry over plain HTTPS+JSON and maps
 * the wire record to our {@link BorrowerInfo}. Same pattern as the animals
 * HttpOwnerRegistryAdapter; locally the URL is the course's mock registry.
 */
public class HttpBorrowerRegistryAdapter implements BorrowerRegistryAdapter {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper json = new ObjectMapper();
    private final String baseUrl;

    public HttpBorrowerRegistryAdapter(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public BorrowerInfo lookup(String isikukood) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/persons/" + isikukood))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                // A person the registry does not know is a miss, not a failure.
                return null;
            }
            if (response.statusCode() != 200) {
                throw new BorrowerRegistryException("borrower registry answered HTTP " + response.statusCode());
            }
            RrPersonResponse wire = json.readValue(response.body(), RrPersonResponse.class);
            // The mapper — the only place where Estonian wire names become our names.
            return new BorrowerInfo(wire.isikukood(), wire.eesnimi(), wire.perekonnanimi());
        } catch (IOException e) {
            throw new BorrowerRegistryException("borrower registry unreachable: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BorrowerRegistryException("borrower registry call interrupted", e);
        }
    }
}
