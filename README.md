# commercetools-java-sdk-v2-training
Training exercises for the commercetools V2 Java SDK

### Configuration

1. Add API Client credentials to `src/main/resources/dev.properties.dist`
2. Change ServiceRegion in `src/main/java/handson/impl/ClientService.java::createApiClient` to the one that your `MerchantCentral` project is using. E.g. for `europe-west1`
    ```
    return ApiRootBuilder.of()
                .defaultClient(ClientCredentials.of()
                        .withClientId(clientId)
                        .withClientSecret(clientSecret)
                        .withScopes(scopes)
                        .build(),
                        ServiceRegion.GCP_EUROPE_WEST1)
                .build(projectKey);
    ```