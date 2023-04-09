package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.InventoryMode;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.common.BaseAddress;
import com.commercetools.api.models.common.BaseAddressBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.discount_code.DiscountCode;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.payment.*;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodPagedQueryResponse;
import com.commercetools.api.models.state.State;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import io.vrap.rmf.base.client.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


/**
 * Create a cart for a customer, add a product to it, create an order from the cart and change the order state.
 *
 * See:
 */
public class Task04b_CHECKOUT {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        CustomerService customerService = new CustomerService(client);
        CartService cartService = new CartService(client);
        OrderService orderService = new OrderService(client);
        PaymentService paymentService = new PaymentService(client);
        ChannelService channelService = new ChannelService(client);
        DiscountCodeService discountCodeService = new DiscountCodeService(client);
        ShippingMethodService shippingMethodService = new ShippingMethodService(client);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

        // TODO: Fetch a channel if your inventory mode will not be NONE
        //
        String channelId = "7e489848-7d5c-4dc9-bfeb-48c238cc587e"; // sunrise-store-amsterdam
        ApiHttpResponse<Channel> channel = channelService.getChannelById(channelId).get();

        // TODO: Perform cart operations:
        //      Get Customer, create cart, add products, add inventory mode
        //      add discount codes, perform a recalculation
        // TODO: Convert cart into an order, set order status, set state in custom work
        //
        // TODO: add payment
        // TAKE CARE: Take off payment for second or third try OR change the interfaceID with a timestamp
        //
        // TODO additionally: add custom line items, add shipping method
        //

        // TODO: 1. Get Customer
        String customerId = "72c66007-cc5f-4424-9902-4af2eeda1426";
        ApiHttpResponse<Customer> customerApiHttpResponse = customerService.getCustomerById(customerId).get();
        ApiHttpResponse<Cart> cartApiHttpResponse = null;
        // TODO: 2. Get/create a cart for the customer

            // Exception handling via exceptionally
        cartApiHttpResponse = cartService
                .getCartByCustomerId(customerId)
                .exceptionally(e -> {
                    if (e.getCause() instanceof NotFoundException) {
                        try {
                            return cartService
                                    .createCart(customerApiHttpResponse.getBody(), InventoryMode.TRACK_ONLY, "EUR")
                                    .get();
                        } catch (InterruptedException | ExecutionException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    return null;
                })
                .get();

// Default exception handling
//        try {
//            cartApiHttpResponse = cartService
//                    .getCartByCustomerId(customerId)
//                    .get();
//        } catch (ExecutionException e) {
//            if (e.getCause() instanceof NotFoundException) {
//                try {
//                    cartApiHttpResponse = cartService
//                            .createCart(customerApiHttpResponse, InventoryMode.TRACK_ONLY)
//                            .get();
//                } catch (InterruptedException | ExecutionException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        }

        if (cartApiHttpResponse != null) {
            logger.info("Cart ID: {}", cartApiHttpResponse.getBody().getId());
            // TODO: 3. Add products to the cart
            String[] skuList = new String[]{
                    "M0E20000000E3GK",
                    "M0E20000000EEYU",
                    "M0E20000000E3YI"
            };
            cartApiHttpResponse = cartService
                    .addProductToCartBySkusAndChannel(cartApiHttpResponse.getBody(), channel.getBody(), skuList)
                    .get();


            // TODO: 4. Set a discount code to the cart
            String discountCodeStr = "TEST";
            DiscountCode discountCode = discountCodeService
                    .getDiscountCodeByCode(discountCodeStr)
                    .get()
                    .getBody()
                    .getResults()
                    .get(0);
            cartApiHttpResponse = cartService.addDiscountToCart(cartApiHttpResponse, discountCode.getCode()).get();

            // TODO: 5. Recalculate cart
            cartApiHttpResponse = cartService.recalculate(cartApiHttpResponse.getBody()).get();

            // TODO: 6. Set shipping address to the cart
            BaseAddress shippingAddress = BaseAddressBuilder.of()
                    .title("My Test Shipping Address")
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
                    .email("vladislavs@scandiweb.com")
                    .fax("+49 89 12345679")
                    .additionalAddressInfo("Additional address info")
                    .externalId("External ID field")
                    .build();
            cartApiHttpResponse = cartService.setShippingAddress(cartApiHttpResponse.getBody(), shippingAddress).get();
            logger.info("Added shipping address: {}", cartApiHttpResponse.getBody().getShippingAddress().getTitle());

            // TODO: 7. Set billing address to the cart
            BaseAddress billingAddress = BaseAddressBuilder.of()
                    .title("My Test Billing Address")
                    .salutation("Mr.")
                    .firstName("Vlad")
                    .lastName("3142")
                    .streetName("Example Street2")
                    .streetNumber("22222")
                    .additionalAddressInfo("Additional street info2")
                    .postalCode("1014")
                    .city("Riga")
                    .region("Riga region")
                    .state("Example state")
                    .country("LV")
                    .company("Scandiweb")
                    .department("Dev2")
                    .building("Building number 12")
                    .apartment("2223")
                    .pOBox("22213")
                    .phone("+371 7424323")
                    .mobile("+371 22232324")
                    .email("vladislavs+2@scandiweb.com")
                    .fax("+49 89 12345672")
                    .additionalAddressInfo("Additional address info 2")
                    .externalId("External ID field 2")
                    .build();
            cartApiHttpResponse = cartService.setBillingAddress(cartApiHttpResponse.getBody(), billingAddress).get();
            logger.info("Added shipping address: {}", cartApiHttpResponse.getBody().getBillingAddress().getTitle());

            // TODO: 8. Set shipping method to the cart and recalculate cart
            //  get available shipping methods and use one
            ApiHttpResponse<ShippingMethodPagedQueryResponse> availableShippingMethods = shippingMethodService
                    .getShippingMethodsForCart(cartApiHttpResponse.getBody())
                    .get();
            ShippingMethod firstAvailableShippingmethod = availableShippingMethods.getBody().getResults().get(0);
            String selectedShippingMethodId = firstAvailableShippingmethod.getId();
            cartApiHttpResponse = cartService.setShippingMethod(cartApiHttpResponse.getBody(), selectedShippingMethodId).get();

            // TODO: 9. Create an initial payment without transactions
            ApiHttpResponse<Payment> paymentApiHttpResponse = paymentService
                    .createPayment(cartApiHttpResponse.getBody(), "MasterCard", "CreditCard")
                    .get();
            logger.info(
                    "Created payment with info: {}, {}",
                    paymentApiHttpResponse.getBody().getPaymentMethodInfo().getMethod(),
                    paymentApiHttpResponse.getBody().getPaymentMethodInfo().getPaymentInterface()
            );

            // TODO: 10. Add payment to the cart
            cartApiHttpResponse = cartService.addPayment(cartApiHttpResponse.getBody(), paymentApiHttpResponse.getBody()).get();
            logger.info("Payment ID in cart = {}", cartApiHttpResponse.getBody().getPaymentInfo().getPayments().get(0).getId());

            // TODO: 11. Convert cart to the order
            //  set default shipment/payment states, default order transition state, order number
            ApiHttpResponse<Order> orderApiHttpResponse = orderService.createOrderFromCart(cartApiHttpResponse.getBody()).get();
            logger.info("Order created from the cart. Order id {}", orderApiHttpResponse.getBody().getId());

            // TODO: 12. Create authorization payment transaction with Initial state
            String paymentInteractionId = "random-interaction-id-from-psp-1";
            paymentApiHttpResponse = paymentService.addTransaction(
                    cartApiHttpResponse.getBody(),
                    paymentApiHttpResponse.getBody(),
                    paymentInteractionId,
                    TransactionType.AUTHORIZATION,
                    "INITIAL"
            ).get();
            List<Transaction> paymentTransactionList = paymentApiHttpResponse.getBody().getTransactions();
            Transaction paymentTransaction = paymentTransactionList.get(paymentTransactionList.size() - 1);
            logger.info("Payment transaction id: {}. State: {}", paymentTransaction.getId(), paymentTransaction.getState().name());

            // TODO: 13. Change transaction state to Pending
            paymentApiHttpResponse = paymentService.setTransactionState(
                    paymentApiHttpResponse.getBody(),
                    paymentTransaction,
                    TransactionState.PENDING
            ).get();
            logger.info("Payment transaction state: {}", paymentApiHttpResponse.getBody().getTransactions().get(0).getState().name());

            // TODO: 14. Change transaction state to Success
            paymentApiHttpResponse = paymentService.setTransactionState(
                    paymentApiHttpResponse.getBody(),
                    paymentTransaction,
                    TransactionState.SUCCESS
            ).get();
            logger.info("Payment transaction state: {}", paymentApiHttpResponse.getBody().getTransactions().get(0).getState().name());

            // TODO: 15. Add charge transaction
            paymentInteractionId = "random-interaction-id-from-psp-2";
            paymentApiHttpResponse = paymentService.addTransaction(
                    cartApiHttpResponse.getBody(),
                    paymentApiHttpResponse.getBody(),
                    paymentInteractionId, 
                    TransactionType.CHARGE,
                    "INITIAL"
            ).get();
            paymentTransactionList = paymentApiHttpResponse.getBody().getTransactions();
            paymentTransaction = paymentTransactionList.get(paymentTransactionList.size() - 1);
            logger.info("Last payment transaction id: {}. State: {}", paymentTransaction.getId(), paymentTransaction.getState().name());

            logger.info(
                    "Created order number: {}. ID: {}",
                    orderApiHttpResponse.getBody().getOrderNumber(),
                    orderApiHttpResponse.getBody().getId()
            );
        }

        client.close();
    }
}
