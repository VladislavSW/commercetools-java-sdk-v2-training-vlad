package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.state.*;
import handson.impl.ApiPrefixHelper;
import handson.impl.StateMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Helper {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Helper.class.getName());
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final StateMachineService stateMachineService = new StateMachineService(client);

//        stateMachineService.createState("OrderCreated", StateTypeEnum.ORDER_STATE, true, "Order Created").get();
//        client
//                .states()
//                        .withId("1d41d73a-139d-4923-868e-853620fce71f")
//                                .post(
//                                        StateUpdateBuilder.of()
//                                                .version(2L)
//                                                .actions(
//                                                        StateChangeInitialActionBuilder.of()
//                                                                .initial(false)
//                                                                .build()
//                                                )
//                                                .build()
//                                )
//                                        .execute();
//        List<StateResourceIdentifier> states  = new ArrayList<>();
//        states.add(
//                StateResourceIdentifierBuilder.of().
//                        id("1d41d73a-139d-4923-868e-853620fce71f")
//                        .build()
//        );
//        states.add(
//                StateResourceIdentifierBuilder.of().
//                        id("4b676b13-166d-407c-8a79-65b8a57e3dc0")
//                        .build()
//        );
//        State orderCreatedState = stateMachineService.getStateByKey("OrderCreated").get().getBody();
//        stateMachineService
//                .setStateTransitions(orderCreatedState, states)
//                .get();

        client.close();
    }
}
