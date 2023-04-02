package handson.impl;

public enum ApiPrefixHelper {
    API_DEV_CLIENT_PREFIX("dev-admin."),
    API_PROJECT_SYNC_CLIENT_PREFIX("dev-project-sync-admin."),
    API_DEV_IMPORT_PREFIX("dev-import-admin."),
    API_STORE_CLIENT_PREFIX("dev-berlin-store-admin."),
    API_ME_CLIENT_PREFIX("dev-me-client."),
    API_STORE_ME_CLIENT_PREFIX("dev-store-me-client.");

    private final String prefix;

    ApiPrefixHelper(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}