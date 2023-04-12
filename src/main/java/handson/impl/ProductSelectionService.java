package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.product.ProductResourceIdentifier;
import com.commercetools.api.models.product.ProductResourceIdentifierBuilder;
import com.commercetools.api.models.product_selection.*;
import com.commercetools.api.models.store.*;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**

 */
public class ProductSelectionService {

    final ProjectApiRoot apiRoot;

    public ProductSelectionService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }


    /**
     * Gets a product selection by key.
     *
     * @return the product selection completion stage
     */
    public CompletableFuture<ApiHttpResponse<ProductSelection>> getProductSelectionByKey(
            final String productSelectionKey
    ) {
        return apiRoot
                .productSelections()
                .withKey(productSelectionKey)
                .get()
                .execute();
    }

    /**
     * Gets a store by key.
     *
     * @return the store completion stage
     */
    public CompletableFuture<ApiHttpResponse<Store>> getStoreByKey(
            final String storeKey
    ) {
        return apiRoot
                .stores()
                .withKey(storeKey)
                .get()
                .execute();
    }

    /**
     * Creates a new product selection.
     *
     * @return the product selection creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<ProductSelection>> createProductSelection(
            final String productSelectionKey,
            final String name
    ) {
        return apiRoot
                .productSelections()
                .post(
                        ProductSelectionDraftBuilder.of()
                                .key(productSelectionKey)
                                .name(s -> s.addValue("en", name))
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<ProductSelection>> addProductToProductSelection(
            final ProductSelection productSelection,
            final String productKey
    ) {
        return apiRoot
                .productSelections()
                .withKey(productSelection.getKey())
                .post(
                        ProductSelectionUpdateBuilder.of()
                                .actions(
                                        ProductSelectionAddProductActionBuilder.of()
                                                .product(p -> p.key(productKey))
                                                .build()
                                )
                                .version(productSelection.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Store>> addProductSelectionToStore(
            final Store store,
            final ProductSelection productSelection
    ) {
        return apiRoot
                .stores()
                .withId(store.getId())
                .post(
                        StoreUpdateBuilder.of()
                                .actions(
                                        StoreAddProductSelectionActionBuilder.of()
                                                .productSelection(p -> p.key(productSelection.getKey()))
                                                .build()
                                )
                                .version(store.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<ProductSelectionProductPagedQueryResponse>> getProductsInProductSelection(
            final String productSelectionKey
    ) {
        return apiRoot
                .productSelections()
                .withKey(productSelectionKey)
                .products()
                .get()
                .withExpand("product")
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<ProductsInStorePagedQueryResponse>> getProductsInStore(
            final String storeKey
    ) {
        return apiRoot
                .inStore(storeKey)
                .productSelectionAssignments()
                .get()
                .execute();
    }
}
