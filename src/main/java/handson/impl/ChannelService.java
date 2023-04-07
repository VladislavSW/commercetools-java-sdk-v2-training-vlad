package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.customer.*;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Customer}s.
 */
public class ChannelService {

    final ProjectApiRoot apiRoot;

    public ChannelService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<Channel>> getChannelById(
            final String channelId
    ) {
        return apiRoot
                .channels()
                .withId(channelId)
                .get()
                .execute();
    }

}
