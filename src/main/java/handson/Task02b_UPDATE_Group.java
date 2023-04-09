package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer_group.CustomerGroup;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
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
public class Task02b_UPDATE_Group {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client);

        // TODO:
        //  GET a customer
        //  GET a customer group
        //  ASSIGN the customer to the customer group
        //
        Customer customer = customerService
                .getCustomerById("72c66007-cc5f-4424-9902-4af2eeda1426")
                .get()
                .getBody();
        logger.info(String.format("Customer assigned to group: %s", customer.getCustomerGroup()));

        CustomerGroup customerGroupKey = customerService
                .getCustomerGroupByKey("diamond")
                .get()
                .getBody();

        Customer updatedCustomer = customerService
                .assignCustomerToCustomerGroup(customer, customerGroupKey)
                .get()
                .getBody();
        logger.info(String.format("Customer assigned to group: %s", updatedCustomer.getCustomerGroup().getId()));

        client.close();
    }

}

