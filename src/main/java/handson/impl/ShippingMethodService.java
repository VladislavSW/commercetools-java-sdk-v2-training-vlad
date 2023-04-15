package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class ShippingMethodService {

    final ProjectApiRoot apiRoot;

    public ShippingMethodService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<ShippingMethod>> getShippingMethodById(
            final String id
    ) {
        return apiRoot
                .shippingMethods()
                .withId(id)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<ShippingMethodPagedQueryResponse>> getShippingMethodsForCart(
            final Cart cart
    ) {
        return apiRoot
                .shippingMethods()
                .matchingCart()
                .get()
                .withCartId(cart.getId())
                .execute();
    }
}
