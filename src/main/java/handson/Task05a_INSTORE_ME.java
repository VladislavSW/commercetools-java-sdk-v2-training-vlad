package handson;

import ch.qos.logback.core.net.server.Client;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.cart.InventoryMode;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import handson.impl.ApiPrefixHelper;

import handson.impl.CartService;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 *
 */
public class Task05a_INSTORE_ME {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task05a_INSTORE_ME.class.getName());

        // TODO: Create in-store cart with global API client
        //  Provide an API client with global permissions
        //  Provide a customer who is restricted to a store
        //  Note: A global cart creation should fail but an in-store cart should world
        //
        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = ClientService.createApiClient(globalApiClientPrefix);
        final CartService globalCartService = new CartService(client);
        final CustomerService globalCustomerService = new CustomerService(client);

        Customer inStoreCustomer = globalCustomerService
                .getCustomerById("21a16139-18c3-4392-8789-59b3a59021c5")
                .get()
                .getBody();
        Customer globalCustomer = globalCustomerService
                .getCustomerById("72c66007-cc5f-4424-9902-4af2eeda1426")
                .get()
                .getBody();

        // The response error using in store bound customer with global API client
        // http response formatted body: {
        //  "statusCode" : 400,
        //  "message" : "The 'store' field must be set to one of the stores the customer is part of.",
        //  "errors" : [ {
        //    "code" : "InvalidOperation",
        //    "message" : "The 'store' field must be set to one of the stores the customer is part of."
        //  } ]
        //}
        // Uncomment next lines to trigger the above error
//        ApiHttpResponse<Cart> cartApiHttpResponse = globalCartService
//                .createCart(inStoreCustomer, InventoryMode.TRACK_ONLY, "EUR")
//                .get();
//        logger.info("Created in-store cart with a global api client: {}", cartApiHttpResponse.getBody().getId());


        // TODO: Create in-store Cart with in-store API client
        //  Update the ApiPrefixHelper with the prefix for Store API Client
        //  Provide an API client with scope limited to a store
        //  Provide a customer with only store permissions
        //  Try creating a global cart with a global customer and check the error message

        final String storeApiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        final String storeKey = "europe";
        final ProjectApiRoot storeClient = ClientService.createApiClient(storeApiClientPrefix);
        final CartService storeCartService = new CartService(storeClient);

        ApiHttpResponse<Cart> cartApiHttpResponse = storeCartService
                .createCartInStore(storeKey, inStoreCustomer, InventoryMode.TRACK_ONLY, "EUR")
                .get();
        logger.info("Created in-store cart with a store api client for an in store customer: {}", cartApiHttpResponse.getBody().getId());
        cartApiHttpResponse = storeCartService
                .createCartInStore(storeKey, globalCustomer, InventoryMode.TRACK_ONLY, "EUR")
                .get();
        logger.info("Created in-store cart with a store api client for a global customer: {}", cartApiHttpResponse.getBody().getId());

        // TODO
        //  Verify on impex that the carts are holding the same information
        //
        // Both global and in store customers will have carts that are bound to the europe store




        // TODO: Create a cart via /me endpoint
        //  Provide API client with SPA for customer with global permissions
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  You can also create in-store customer-bound cart
        //  Visit impex to inspect the carts created

        final String meApiClientPrefix = ApiPrefixHelper.API_ME_CLIENT_PREFIX.getPrefix();
        final String customerEmail = "vladislavs+meglobal@scandiweb.com";
        final ProjectApiRoot meClient = ClientService.createMeTokenApiClient(
                meApiClientPrefix,
                customerEmail,
                "password"
        );

        logger.info("Get cart for customer via me endpoint: " +
                meClient
                        .me()
                        .carts()
                        .post(
                                MyCartDraftBuilder.of()
                                                  .currency("EUR")
                                                  .deleteDaysAfterLastModification(90L)
                                                  .customerEmail(customerEmail)
                                                  .build()
                        )
                        .execute()
                        .exceptionally(throwable -> {
                            logger.info(throwable.getLocalizedMessage());
                            return null;
                        })
                        .toCompletableFuture().get()
                        .getBody().getId()
        );
        meClient.close();

        // TODO: Create in-store customer-bound Cart with in-store-me API client
        //  Update the ApiPrefixHelper with the prefix for Me(SPA) API Client
        //  Provide in-store-me API client with scope for a store and me endpoint
        //  Try creating a global cart without me and check the error message
        //  Visit impex to inspect the carts created
        final String storeMeApiClientPrefix = ApiPrefixHelper.API_STORE_ME_CLIENT_PREFIX.getPrefix();
        final String storeCustomerEmail = "vladislavs+mestore@scandiweb.com";
        final ProjectApiRoot meStoreClient = ClientService.createStoreMeApiClient(
                storeMeApiClientPrefix,
                storeCustomerEmail,
                "password"
        );
        final String meStoreKey = ClientService.getStoreKey(storeMeApiClientPrefix);

//        logger.info(
//                "Created global cart with Me API client without me endpoint: {}",
//                meStoreClient
//                        .carts()
//                        .post(
//                                CartDraftBuilder.of()
//                                        .deleteDaysAfterLastModification(1L)
//                                        .currency("EUR")
//                                        .customerEmail(storeCustomerEmail)
//                                        .build()
//                        )
//                        .execute()
//                        .exceptionally(t -> { logger.info(t.getLocalizedMessage()); return null; })
//                        .get()
//                        .getBody()
//                        .getId()
//        );
        // The above will produce the following error:
//        http response formatted body: {
//            "statusCode" : 403,
//                    "message" : "Insufficient scope. One of the following scopes is missing: manage_orders.",
//                    "errors" : [ {
//                "code" : "insufficient_scope",
//                        "message" : "Insufficient scope. One of the following scopes is missing: manage_orders."
//            } ],
//            "error" : "insufficient_scope",
//                    "error_description" : "Insufficient scope. One of the following scopes is missing: manage_orders."
//        }
        // This is because Me API client has only *_my_* (Manage My) scopes

        logger.info("Created in-store cart with a store api client: "+
                meStoreClient
                        .inStore(meStoreKey)
                        .me()
                        .carts()
                        .post(
                                MyCartDraftBuilder.of()
                                        .deleteDaysAfterLastModification(90L)
                                        .currency("EUR")
                                        .customerEmail(storeCustomerEmail)
                                        .build()
                        )
                        .execute()
                        .exceptionally(throwable -> {
                            logger.info(throwable.getLocalizedMessage().toString());
                            return null;
                        })
                        .toCompletableFuture().get()
                        .getBody().getId()
        );
        // The in-store cart has "store" property e.g.:
//        ...
//        "origin": "Customer",
//        "itemShippingAddresses": [],
//        "store": {
//            "typeId": "store",
//             "key": "europe"
//        }

        storeClient.close();

        client.close();

    }
}
