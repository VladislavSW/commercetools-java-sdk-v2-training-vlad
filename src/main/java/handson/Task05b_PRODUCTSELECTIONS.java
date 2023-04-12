package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product_selection.AssignedProductReference;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.store.Store;
import handson.impl.ApiPrefixHelper;
import handson.impl.ProductSelectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


/**
 *
 */
public class Task05b_PRODUCTSELECTIONS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task05b_PRODUCTSELECTIONS.class.getName());
        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(globalApiClientPrefix);

        final ProductSelectionService productSelectionService = new ProductSelectionService(client);

        final String productSelectionKey = "task05b_product_selection";

        // TODO: Create product selection and add a product to the product selection
        ProductSelection productSelection = productSelectionService
                .createProductSelection(productSelectionKey, "Programmatically Created Product Selection")
                .get()
                .getBody();
        logger.info("Created product selection: " + productSelection.getId());



        // TODO: Get a store and assign the product selection to the store
        Store store = productSelectionService
                .getStoreByKey("europe")
                .get()
                .getBody();


        logger.info("Product selections assigned to the store: "
                + productSelectionService.addProductSelectionToStore(store, productSelection)
        );


        // TODO Get products in a product selection
        productSelection = productSelectionService
                .addProductToProductSelection(productSelection, "73218")
                .get()
                .getBody();

        List<AssignedProductReference> assignedProductReferences = productSelectionService
                .getProductsInProductSelection(productSelectionKey)
                .get()
                .getBody()
                .getResults();
        assignedProductReferences.forEach(assignedProductReference -> logger.info(assignedProductReference.getProduct().getObj().getKey()));
    }
}
