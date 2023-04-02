package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerToken;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        /**
         * TODO:
         * UPDATE the ApiPrefixHelper with your prefix from dev.properties (e.g. "dev-admin.")
         */
        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Task02a_CREATE.class.getName());
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client);

        Customer existingCustomer = customerService
                .getCustomerByKey("SOME_GENERATED_KEY_FROM_CUSTOMER_DATA")
                .get()
                .getBody();

        logger.info("Customer fetch: " + existingCustomer.getEmail());

            // TODO:
            //  CREATE a customer
            //  CREATE a email verification token
            //  Verify customer
            //
        Customer createdCustomer = customerService
                .createCustomer(
                        "vladislavs+javatest@scandiweb.com",
                        "Option123#",
                        "vladislavs_javatest_scandiweb_com",
                        "Vlad",
                        "314",
                        "LV"
                )
                .get()
                .getBody()
                .getCustomer();
        logger.info(
                String.format(
                        "Customer created. Email: %s; Firstname: %s; Lastname: %s",
                        createdCustomer.getEmail(),
                        createdCustomer.getFirstName(),
                        createdCustomer.getLastName()
                )
        );

        CustomerToken customerToken = customerService
                .createEmailVerificationToken(createdCustomer, 7200)
                .get()
                .getBody();
        Customer verifiedCustomer = customerService
                .verifyEmail(customerToken)
                .get()
                .getBody();
        logger.info(
                String.format(
                        "Is customer verified: %s",
                        verifiedCustomer.getIsEmailVerified()
                )
        );

        client.close();
    }
}
