package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartAddPaymentActionBuilder;
import com.commercetools.api.models.cart.CartUpdateBuilder;
import com.commercetools.api.models.common.MoneyBuilder;
import com.commercetools.api.models.common.MoneyType;
import com.commercetools.api.models.payment.*;
import com.commercetools.api.models.product_type.AttributeTextType;
import com.commercetools.api.models.product_type.AttributeTextTypeBuilder;
import com.commercetools.api.models.type.TypeResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.awt.font.TextAttribute;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;


// TODO: Change order of actions
// a) Create Payment
// b) Set Payment on card
// c) Add Transactions

// TODO: Add Interface actions
// d) Requires the declaration of custom fields (taught in a later session in class)

// TODO: Allow the customer to play with different payment methods
// Have an order number like order1234-12, where -12 is not visible to the customer

public class PaymentService {

    final ProjectApiRoot apiRoot;

    public PaymentService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createPaymentAndAddToCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String psp_Name,
            final String psp_Method,
            final String interfaceId
    ) {
        final Cart cart = cartApiHttpResponse.getBody();

        return apiRoot
                .payments()
                .post(
                        PaymentDraftBuilder.of()
                                .amountPlanned(
                                        MoneyBuilder.of()
                                            .centAmount(cart.getTotalPrice().getCentAmount())
                                            .currencyCode(cart.getTotalPrice().getCurrencyCode())
                                            .build()
                                )
                                .paymentMethodInfo(
                                        PaymentMethodInfoBuilder.of()
                                                .paymentInterface(psp_Name)        // PSP Provider Name: WireCard, ....
                                                .method(psp_Method)                // PSP Provider Method: CreditCard
                                                .build())
                                .interfaceId(interfaceId)                          // ID of the payment, important !!!
                                .build()
                )
                .execute()
                .thenComposeAsync(paymentApiHttpResponse ->
                            apiRoot
                                    .carts()
                                    .withId(cart.getId())
                                    .post(
                                            CartUpdateBuilder.of()
                                                    .version(cart.getVersion())
                                                    .actions(
                                                        CartAddPaymentActionBuilder.of()
                                                            .payment(
                                                                    PaymentResourceIdentifierBuilder.of()
                                                                        .id(paymentApiHttpResponse.getBody().getId())
                                                                        .build()
                                                            )
                                                            .build()
                                                    )
                                                    .build()
                                    )
                                    .execute()
                        );
    }

    public CompletableFuture<ApiHttpResponse<Payment>> createPayment(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String psp_Name,
            final String psp_Method
    ) {
        final Cart cart = cartApiHttpResponse.getBody();

        return apiRoot
                .payments()
                .post(
                        PaymentDraftBuilder.of()
                                .amountPlanned(
                                        MoneyBuilder.of()
                                                .centAmount(cart.getTotalPrice().getCentAmount())
                                                .currencyCode(cart.getTotalPrice().getCurrencyCode())
                                                .build()
                                )
                                .paymentMethodInfo(
                                        PaymentMethodInfoBuilder.of()
                                                .paymentInterface(psp_Name)        // PSP Provider Name: WireCard, ....
                                                .method(psp_Method)                // PSP Provider Method: CreditCard
                                                .build())
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Payment>> setInterfaceId(
            final ApiHttpResponse<Payment> paymentApiHttpResponse,
            final String interfaceId
    ) {
        Payment payment = paymentApiHttpResponse.getBody();

        return apiRoot
                .payments()
                .withId(payment.getId())
                .post(
                        PaymentUpdateBuilder.of()
                                .version(payment.getVersion())
                                .actions(
                                        PaymentSetInterfaceIdActionBuilder.of()
                                                .interfaceId(interfaceId)
                                                .build()
                                )
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Payment>> addTransaction(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final ApiHttpResponse<Payment> paymentApiHttpResponse,
            final String interactionId,
            final TransactionType transactionType,
            final String pspStatusCode
    ) {
        Cart cart = cartApiHttpResponse.getBody();
        Payment payment = paymentApiHttpResponse.getBody();

        return apiRoot
                .payments()
                .withId(payment.getId())
                .post(
                        PaymentUpdateBuilder.of()
                                .version(payment.getVersion())
                                .actions(
                                        PaymentAddTransactionActionBuilder.of()
                                                .transaction(
                                                        TransactionDraftBuilder.of()
                                                                .amount(
                                                                        MoneyBuilder.of()
                                                                                .centAmount(cart.getTotalPrice().getCentAmount())
                                                                                .currencyCode(cart.getTotalPrice().getCurrencyCode())
                                                                                .build()
                                                                )
                                                                .timestamp(ZonedDateTime.now())
                                                                .type(transactionType)
                                                                .interactionId(interactionId)
                                                                .build()
                                                )
                                                .build(),
                                        PaymentAddInterfaceInteractionActionBuilder.of()
                                                .fields(f -> f.addValue("payment_interaction_text", String.format("Custom payment interaction text %s", transactionType.name())))
                                                .type(t -> t.key("paymentInteractionText"))
                                                .build(),
                                        PaymentSetStatusInterfaceCodeActionBuilder.of()
                                                .interfaceCode(pspStatusCode)
                                                .build(),
                                        PaymentSetStatusInterfaceTextActionBuilder.of()
                                                .interfaceText(String.format("Transaction type: %s", transactionType.name()))
                                                .build()
                                )
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Payment>> setTransactionState(
            final ApiHttpResponse<Payment> paymentApiHttpResponse,
            final Transaction transaction,
            final TransactionState toState
    ) {
        Payment payment = paymentApiHttpResponse.getBody();

        return apiRoot
                .payments()
                .withId(payment.getId())
                .post(
                        PaymentUpdateBuilder.of()
                                .version(payment.getVersion())
                                .actions(
                                        PaymentChangeTransactionStateActionBuilder.of()
                                                .transactionId(transaction.getId())
                                                .state(toState)
                                                .build()
                                )
                                .build()
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<PaymentPagedQueryResponse>> getPayments() {
        return apiRoot
                .payments()
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Payment>> deletePayment(
            final ApiHttpResponse<Payment> paymentApiHttpResponse
    ) {
        Payment payment = paymentApiHttpResponse.getBody();

        return apiRoot
                .payments()
                .withId(payment.getId())
                .delete()
                .addVersion(payment.getVersion())
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Payment>> deletePayment(
            final Payment payment
    ) {
        return apiRoot
                .payments()
                .withId(payment.getId())
                .delete()
                .addVersion(payment.getVersion())
                .execute();
    }

}
