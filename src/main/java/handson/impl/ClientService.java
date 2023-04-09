package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiRootBuilder;
import io.vrap.rmf.base.client.AuthenticationToken;
import io.vrap.rmf.base.client.HttpClientSupplier;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.oauth2.ClientCredentialsTokenSupplier;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ClientService {

    public static ProjectApiRoot projectApiRoot;
    public static com.commercetools.importapi.client.ProjectApiRoot importApiRoot;
    public static final String DEV_PROPERTIES_FILE_PATH = "/dev.properties.dist";

    /**
     * @throws IOException exception
     */
    public static ProjectApiRoot createApiClient(
            final String prefix
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
        String projectKey = prop.getProperty(prefix + "projectKey");
        String scopes = prop.getProperty(prefix + "scopes");

        return ApiRootBuilder.of()
                .defaultClient(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .withScopes(scopes)
                                .build(),
                        ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1
                )
                .build(projectKey);
    }

    public static String getProjectKey(
            final String prefix
    ) throws IOException {
        return null;
    }

    public static String getClientId(
            final String prefix
    ) throws IOException {
        return null;
    }

    public static String getClientSecret(
            final String prefix
    ) throws IOException {
        return null;
    }

    public static String getStoreKey(
            final String prefix
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));

        return prop.getProperty(prefix + "storeKey");
    }

    public static String getCustomerEmail(
            final String prefix
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));

        return prop.getProperty(prefix + "customerEmail");
    }

    /**
     * @return apiRoot
     * @throws IOException exception
     */
    public static com.commercetools.importapi.client.ProjectApiRoot createImportApiClient(
            final String prefix
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
        String projectKey = prop.getProperty(prefix + "projectKey");

        return ImportApiRootBuilder.of()
                .defaultClient(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getOAuthTokenUrl(),
                        com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getApiUrl()
                )
                .build(projectKey);
    }

    public static ProjectApiRoot createMeTokenApiClient(
            final String prefix,
            final String customerEmail,
            final String customerPassword
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        return ApiRootBuilder.of()
                .defaultClient(
                        ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getApiUrl()
                )
                .withGlobalCustomerPasswordFlow(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        customerEmail,
                        customerPassword,
                        ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getAuthUrl() + "/oauth/" + projectKey + "/customers/token"
                )
                .build(projectKey);
    }

    public static ProjectApiRoot createStoreMeApiClient(
            final String prefix,
            final String storeCustomerEmail,
            final String storeCustomerPassword
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String storeKey = prop.getProperty(prefix + "storeKey");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        return ApiRootBuilder.of().defaultClient(ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getApiUrl())
                .withGlobalCustomerPasswordFlow(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        storeCustomerEmail,
                        storeCustomerPassword,
                        ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getAuthUrl() + "/oauth/" + projectKey + "/in-store/key=" + storeKey + "/customers/token"
                )
                .build(projectKey);
    }

    public static AuthenticationToken getTokenForClientCredentialsFlow(
            final String prefix
    ) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream(DEV_PROPERTIES_FILE_PATH));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
        AuthenticationToken token = null;
        try (final ClientCredentialsTokenSupplier clientCredentialsTokenSupplier = new ClientCredentialsTokenSupplier(
                clientId,
                clientSecret,
                null,
                ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1.getOAuthTokenUrl(),
                HttpClientSupplier.of().get()
        )) {
            token = clientCredentialsTokenSupplier.getToken().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return token;
    }

}
