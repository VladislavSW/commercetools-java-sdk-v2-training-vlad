package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.discount_code.DiscountCode;
import com.commercetools.api.models.discount_code.DiscountCodePagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class DiscountCodeService {

    final ProjectApiRoot apiRoot;

    public DiscountCodeService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<DiscountCode>> getDiscountCodeById(final String id)
    {

        return apiRoot
                .discountCodes()
                .withId(id)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<DiscountCodePagedQueryResponse>> getDiscountCodeByCode(final String code) {

        return apiRoot
                .discountCodes()
                .get()
                .withWhere("code = :discountCode")
                .withPredicateVar("discountCode", code)
                .execute();
    }

}
