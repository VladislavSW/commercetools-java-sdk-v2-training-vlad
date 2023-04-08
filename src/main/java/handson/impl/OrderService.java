package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.*;
import com.commercetools.api.models.state.State;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class OrderService {

    final ProjectApiRoot apiRoot;

    public OrderService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<Order>> getOrderById(
            final String orderId
    ) {
        return apiRoot
                .orders()
                .withId(orderId)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Order>> createOrderFromCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse
    ) {
        final Cart cart = cartApiHttpResponse.getBody();
        final String defaultOrderTransitionState = "OrderCreated";

        return apiRoot
                .orders()
                .post(
                        OrderFromCartDraftBuilder.of()
                                .cart(c -> c.id(cart.getId()))
                                .version(cart.getVersion())
                                .build()
                )
                .execute()
                .thenComposeAsync(orderApiHttpResponse -> {
                    Order order = orderApiHttpResponse.getBody();

                    return apiRoot
                            .orders()
                            .withId(order.getId())
                            .post(
                                    OrderUpdateBuilder.of()
                                            .version(order.getVersion())
                                            .actions(
                                                    OrderChangeShipmentStateActionBuilder.of()
                                                            .shipmentState(ShipmentState.PENDING)
                                                            .build(),
                                                    OrderChangePaymentStateActionBuilder.of()
                                                            .paymentState(PaymentState.PENDING)
                                                            .build(),
                                                    OrderTransitionStateActionBuilder.of()
                                                            .state(s -> s.key(defaultOrderTransitionState))
                                                            .build(),
                                                    OrderSetOrderNumberActionBuilder.of()
                                                            .orderNumber(String.format("ORD_%s", order.getId()))
                                                            .build()
                                            )
                                            .build()
                            )
                            .execute();
                });
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final OrderState state
    ) {
        Order order = orderApiHttpResponse.getBody();

        return apiRoot
               .orders()
               .withId(order.getId())
                .post(
                        OrderUpdateBuilder.of()
                                .actions(
                                        OrderChangeOrderStateActionBuilder.of()
                                                .orderState(state)
                                                .build()
                                )
                                .version(order.getVersion())
                                .build()
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeWorkflowState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final State workflowState
    ) {
        final Order order = orderApiHttpResponse.getBody();

        return apiRoot
                .orders()
                .withId(order.getId())
                .post(
                        OrderUpdateBuilder.of()
                                .actions(
                                        OrderTransitionStateActionBuilder.of()
                                                .state(s -> s.id(workflowState.getId()))
                                                .build()
                                )
                                .version(order.getVersion())
                                .build()
                )
                .execute();
    }

}
