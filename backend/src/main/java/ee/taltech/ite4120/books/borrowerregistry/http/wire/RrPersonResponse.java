package ee.taltech.ite4120.books.borrowerregistry.http.wire;

/**
 * WIRE RECORD — the population registry's response exactly as the registry
 * defines it. Estonian field names on purpose: they must match the provider's
 * schema. They become our English names only in HttpBorrowerRegistryAdapter.
 */
public record RrPersonResponse(
        String isikukood,
        String eesnimi,
        String perekonnanimi,
        String aadress) {}
