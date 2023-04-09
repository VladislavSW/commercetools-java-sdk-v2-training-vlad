package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.common.BaseAddress;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.payment.Payment;
import com.commercetools.api.models.payment.PaymentResourceIdentifierBuilder;
import com.commercetools.api.models.shipping_method.ShippingMethodResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**

 */
public class CartService {

    final ProjectApiRoot apiRoot;

    public CartService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }


    /**
     * Creates a cart for the given customer.
     *
     * @return the customer creation completion stage
     */

    public CompletableFuture<ApiHttpResponse<Cart>> getCartById(
            final String cartId
    ) {
        return apiRoot
                .carts()
                .withId(cartId)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> getCartByCustomerId(
            final String customerId
    ) {
        return apiRoot
                .carts()
                .withCustomerId(customerId)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createCart(
            final Customer customer
    ) {
        return apiRoot
                .carts()
                .post(
                        CartDraftBuilder.of()
                                .currency("EUR")
                                .customerId(customer.getId())
                                .customerEmail(customer.getEmail())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createCart(
            final Customer customer,
            final InventoryMode inventoryMode
    ) {
        return apiRoot
                .carts()
                .post(
                        CartDraftBuilder.of()
                                .currency("EUR")
                                .customerId(customer.getId())
                                .customerEmail(customer.getEmail())
                                .inventoryMode(inventoryMode)
                                .build()
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart() {
        return apiRoot
                .carts()
                .post(
                        CartDraftBuilder.of()
                                .currency("EUR")
                                .deleteDaysAfterLastModification(90L)
                                .anonymousId("an" + System.nanoTime())
                                .country("DE")
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart(
            final String currency,
            final String country
    ) {
        return apiRoot
                .carts()
                .post(
                        CartDraftBuilder.of()
                                .currency(currency)
                                .deleteDaysAfterLastModification(90L)
                                .anonymousId("an" + System.nanoTime())
                                .country(country)
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> deleteCart(
            final Cart cart
    ) {
        return apiRoot
                .carts()
                .withId(cart.getId())
                .delete()
                .addVersion(cart.getVersion())
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final Cart cart,
            final Channel channel,
            final String ... skus
    ) {
        List<CartUpdateAction> cartUpdateActions = new ArrayList<>();

        for (String sku : skus) {
            cartUpdateActions.add(
                    CartAddLineItemActionBuilder.of()
                            .sku(sku)
                            .supplyChannel(c -> c.id(channel.getId()))
                            .build()
            );
        }

        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                .actions(cartUpdateActions)
                                .version(cart.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String code
    ) {
        Cart cart = cartApiHttpResponse.getBody();

        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                .actions(
                                        CartAddDiscountCodeActionBuilder.of()
                                                .code(code)
                                                .build()
                                )
                                .version(cart.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(
            final Cart cart
    ) {
        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                .actions(
                                        CartRecalculateActionBuilder.of()
                                                .updateProductData(false)
                                                .build()
                                )
                                .version(cart.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShippingAddress(
            final Cart cart,
            final BaseAddress address
    ) {
        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                        .actions(
                                                CartSetShippingAddressActionBuilder.of()
                                                        .address(address)
                                                        .build()
                                        )
                                .version(cart.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setBillingAddress(
            final Cart cart,
            final BaseAddress address
    ) {
        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                .actions(
                                        CartSetBillingAddressActionBuilder.of()
                                                .address(address)
                                                .build()
                                )
                                .version(cart.getVersion())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShippingMethod(
            final Cart cart,
            final String shippingMethodId
    ) {
        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                .version(cart.getVersion())
                                .actions(
                                        CartSetShippingMethodActionBuilder.of()
                                                .shippingMethod(
                                                        ShippingMethodResourceIdentifierBuilder.of()
                                                                .id(shippingMethodId)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addPayment(
            final Cart cart,
            final Payment payment
    ) {
        return apiRoot
                .carts()
                .withId(cart.getId())
                .post(
                        CartUpdateBuilder.of()
                                .version(cart.getVersion())
                                .actions(
                                        CartAddPaymentActionBuilder.of()
                                                .payment(
                                                        PaymentResourceIdentifierBuilder.of()
                                                                .id(payment.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .execute();
    }
}
