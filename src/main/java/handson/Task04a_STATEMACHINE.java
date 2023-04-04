package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateResourceIdentifier;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import com.commercetools.api.models.state.StateTypeEnum;
import handson.impl.ApiPrefixHelper;
import handson.impl.StateMachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task04a_STATEMACHINE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Task04a_STATEMACHINE.class.getName());
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final StateMachineService stateMachineService = new StateMachineService(client);

        // TODO
        // Use StateMachineService.java to create your designed order state machine
        // In cse the state key already exists either rename it either delete state transitions and states
        State orderPackedState =
                stateMachineService.createState("OrderPacked", StateTypeEnum.ORDER_STATE, true, "Order Packed")
                        .get()
                        .getBody();
        State orderShippedState =
                stateMachineService.createState("OrderShipped", StateTypeEnum.ORDER_STATE, false, "Order Shipped")
                        .get()
                        .getBody();

        // Add transition
        List<StateResourceIdentifier> states  = new ArrayList<>();
        states.add(
                StateResourceIdentifierBuilder.of().
                        id(orderShippedState.getId())
                        .build()
        );
        logger.info("State info {}",
                stateMachineService
                        .setStateTransitions(orderPackedState, states)
                        .get()
        );

        client.close();
    }
}
