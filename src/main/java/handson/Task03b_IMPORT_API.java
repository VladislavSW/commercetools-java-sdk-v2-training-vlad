package handson;


import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import com.commercetools.importapi.models.importcontainers.ImportContainer;
import com.commercetools.importapi.models.importoperations.ImportOperationStatus;
import com.commercetools.importapi.models.importsummaries.OperationStates;
import handson.impl.ApiPrefixHelper;
import handson.impl.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createImportApiClient;


public class Task03b_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO
        //  Update your prefix for an Import Api Client in the PrefixHelper
        //  Provide a container key
        //
        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_IMPORT_PREFIX.getPrefix();
        final String containerKey = "dev-prices-import-container";

        Logger logger = LoggerFactory.getLogger(Task03b_IMPORT_API.class.getName());
        final ProjectApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client);

        // TODO
        //  CREATE an import container
        //  CREATE a price import request
        //  CHECK the status of your import requests
        //
        try {
            final ImportContainer importContainer = importService.getImportContainer(containerKey);
            logger.info("Import container already exists.");
        } catch (Exception e) {
            logger.info("Created import container {} ",
                    importService.createImportContainer(containerKey)
                            .toCompletableFuture().get()
            );
        }


        // TODO import attribute money variant patch
        Money moneyAttributeAmount = MoneyBuilder.of()
                .centAmount(4200L)
                .currencyCode("EUR")
                .build();

        logger.info("Created price resource {} ",
                importService.createMoneyAttributeImportRequest(
                        containerKey,
                                "A0E200000001YWY",
                                "money-test",
                                moneyAttributeAmount
                        )
                        .get()
                        .getBody()
                        .getOperationStatus()
                        .stream()
                        .map(ImportOperationStatus::getOperationId)
        );

        // TODO import new variant price
        Money amount = MoneyBuilder.of()
                .centAmount(7200L)
                .currencyCode("EUR")
                .build();

        logger.info("Created price resource {} ",
                importService.createPriceImportRequest(
                                containerKey,
                                "73218",
                                "A0E200000001YWY",
                                "38a5131e-16c6-4da6-84fb-feac2cba899a",
                                amount
                        )
                        .get()
                        .getBody()
                        .getOperationStatus()
                        .stream()
                        .map(ImportOperationStatus::getOperationId)
        );

        logger.info("Total containers in our project: {}",
                client
                        .importContainers()
                        .get()
                        .execute()
                        .toCompletableFuture().get()
                        .getBody().getTotal()
        );
        OperationStates states = client
                .importContainers()
                .withImportContainerKeyValue(containerKey)
                .importSummaries()
                .get()
                .execute()
                .toCompletableFuture().get()
                .getBody().getStates();

        logger.info("Processing: {} Imported: {} Unresolved: {} ",
                states.getProcessing(),
                states.getImported(),
                states.getUnresolved()
        );

        client.close();
    }
}

