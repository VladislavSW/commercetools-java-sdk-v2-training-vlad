package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.cart.InventoryMode;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.common.BaseAddress;
import com.commercetools.api.models.common.BaseAddressBuilder;
import com.commercetools.api.models.customer.AnonymousCartSignInMode;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerSigninBuilder;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.payment.Payment;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodPagedQueryResponse;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task04c_CART_MERGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        CustomerService customerService = new CustomerService(client);
        CartService cartService = new CartService(client);
        ChannelService channelService = new ChannelService(client);
        ShippingMethodService shippingMethodService = new ShippingMethodService(client);
        PaymentService paymentService = new PaymentService(client);
        OrderService orderService = new OrderService(client);
        Logger logger = LoggerFactory.getLogger(Task04c_CART_MERGING.class.getName());

        // TODO:    Inspect cart merging
        //          Complete the checkout by adding products, payment, ... test
        //          In a result an order will be created. The order will have 2 items that were merged from anonymous
        //          and existing cart

        // Get a customer and create a cart for this customer
        //
        Customer customer = customerService
                .getCustomerByKey("vladislavs_javatest_scandiweb_com")
                .get()
                .getBody();
        ApiHttpResponse<Cart> cartApiHttpResponse = cartService
                .createCart(customer, InventoryMode.TRACK_ONLY, "EUR")
                .get();
        logger.info("cart-id: " + cartApiHttpResponse.getBody().getId());

        // Add a product to the customer cart
        //
        String channelId = "7e489848-7d5c-4dc9-bfeb-48c238cc587e"; // sunrise-store-amsterdam
        Channel channel = channelService.getChannelById(channelId).get().getBody();
        String[] skuList = new String[]{
                "M0E20000000E3GK"
        };
        cartApiHttpResponse = cartService.addProductToCartBySkusAndChannel(cartApiHttpResponse.getBody(), channel, skuList).get();


        // Create an anonymous cart
        //
        ApiHttpResponse<Cart> anonymousCartApiHttpResponse = cartService
                .createAnonymousCart("EUR", "LV")
                .toCompletableFuture()
                .get();
        logger.info("cart-id-anonymous: " + anonymousCartApiHttpResponse.getBody().getId());

        // Add a product to the anonymous cart
        //
        skuList = new String[]{
                "M0E20000000EEYU"
        };
        anonymousCartApiHttpResponse = cartService.addProductToCartBySkusAndChannel(anonymousCartApiHttpResponse.getBody(), channel, skuList).get();


        // TODO: Decide on a merging strategy
        //
        Cart mergedCart = client
                .login()
                .post(
                        CustomerSigninBuilder.of()
                                .anonymousCartSignInMode(AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART) // Switch to USE_AS_NEW_ACTIVE_CUSTOMER_CART and notice the difference
                                .email("vladislavs+javatest@scandiweb.com")
                                .password("password")
                                .anonymousCart(
                                        CartResourceIdentifierBuilder.of()
                                            .id(anonymousCartApiHttpResponse.getBody().getId())
                                            .build()
                                )
                                .build()
                )
                .execute()
                .toCompletableFuture()
                .get()
                .getBody()
                .getCart();
        logger.info("cart-id-after_merge: " + mergedCart.getId());

        // TODO: Inspect the customers carts here or via impex
        //


        // TODO: place an order

        BaseAddress shippingAndBillingAddress = BaseAddressBuilder.of()
                .title("My Test Address")
                .salutation("Mr.")
                .firstName("Vlad")
                .lastName("314")
                .streetName("Example Street")
                .streetNumber("2222")
                .additionalAddressInfo("Additional street info")
                .postalCode("1013")
                .city("Riga")
                .region("Riga region")
                .state("Example state")
                .country("LV")
                .company("Scandiweb")
                .department("Dev")
                .building("Building number 1")
                .apartment("222")
                .pOBox("2221")
                .phone("+371 7424322")
                .mobile("+371 22232323")
                .email("vladislavs+javatest@scandiweb.com")
                .fax("+49 89 12345679")
                .additionalAddressInfo("Additional address info")
                .externalId("External ID field")
                .build();
        cartApiHttpResponse = cartService.setShippingAddress(mergedCart, shippingAndBillingAddress).get();
        cartApiHttpResponse = cartService.setBillingAddress(cartApiHttpResponse.getBody(), shippingAndBillingAddress).get();
        // Set shipping
        ApiHttpResponse<ShippingMethodPagedQueryResponse> availableShippingMethods = shippingMethodService
                .getShippingMethodsForCart(cartApiHttpResponse.getBody())
                .get();
        ShippingMethod firstAvailableShippingmethod = availableShippingMethods.getBody().getResults().get(0);
        String selectedShippingMethodId = firstAvailableShippingmethod.getId();
        cartApiHttpResponse = cartService.setShippingMethod(cartApiHttpResponse.getBody(), selectedShippingMethodId).get();

        // Add payment
        ApiHttpResponse<Payment> paymentApiHttpResponse = paymentService
                .createPayment(cartApiHttpResponse.getBody(), "MasterCard", "CreditCard")
                .get();
        cartApiHttpResponse = cartService.addPayment(cartApiHttpResponse.getBody(), paymentApiHttpResponse.getBody()).get();

        // Convert cart to order
        ApiHttpResponse<Order> orderApiHttpResponse = orderService.createOrderFromCart(cartApiHttpResponse.getBody()).get();

        client.close();
    }
}

