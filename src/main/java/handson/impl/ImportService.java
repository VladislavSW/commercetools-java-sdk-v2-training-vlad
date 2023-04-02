package handson.impl;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.*;
import com.commercetools.importapi.models.importcontainers.ImportContainer;
import com.commercetools.importapi.models.importcontainers.ImportContainerDraftBuilder;
import com.commercetools.importapi.models.importrequests.*;
import com.commercetools.importapi.models.prices.PriceImportBuilder;
import com.commercetools.importapi.models.productvariants.*;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class ImportService {

    final ProjectApiRoot apiRoot;

    public ImportService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<ImportContainer>> createImportContainer(final String containerKey) {

        return apiRoot
                .importContainers()
                .post(
                        ImportContainerDraftBuilder.of()
                                .key(containerKey)
                                .resourceType(ImportResourceType.PRICE)
                                .build()
                )
                .execute();
    }

    public ImportContainer getImportContainer(final String containerKey)
            throws ExecutionException, InterruptedException {

        return apiRoot
                .importContainers()
                .withImportContainerKeyValue(containerKey)
                .get()
                .execute()
                .get()
                .getBody();
    }


    public CompletableFuture<ApiHttpResponse<ImportResponse>> createMoneyAttributeImportRequest(
            final String containerKey,
            final String productVariantKey,
            final String attributeKey,
            final Money amount) {

        ProductVariantPatch productVariantPatch = ProductVariantPatchBuilder.of()
                .productVariant(p -> p.key(productVariantKey))
                .attributes(a -> a.addValue(
                        attributeKey,
                        MoneyAttributeBuilder.of()
                                .value(amount)
                                .build()
                ))
                .build();

        return apiRoot
                .productVariantPatches()
                .importContainers()
                .withImportContainerKeyValue(containerKey)
                .post(
                        ProductVariantPatchRequestBuilder.of()
                                .patches(productVariantPatch)
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<ImportResponse>> createPriceImportRequest(
            final String containerKey,
            final String productKey,
            final String productVariantKey,
            final String priceKey,
            final Money amount) {

        return apiRoot
                .prices()
                .importContainers()
                .withImportContainerKeyValue(containerKey)
                .post(
                        PriceImportRequestBuilder.of()
                                .resources(
                                        PriceImportBuilder.of()
                                                .key(priceKey)
                                                .product(p -> p.key(productKey))
                                                .productVariant(pv -> pv.key(productVariantKey))
                                                .country("LV")
                                                .validFrom(ZonedDateTime.now())
                                                .validUntil(ZonedDateTime.now().plusDays(2L))
                                                .value(amount)
                                                .build()
                                )
                                .build()
                )
                .execute();

    }

}
