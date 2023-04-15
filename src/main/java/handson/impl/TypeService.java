package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.type.Type;
import com.commercetools.api.models.type.TypeDraft;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Type}s.
 */
public class TypeService {

    final ProjectApiRoot apiRoot;

    public TypeService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<Type>> createType(
            final TypeDraft typeDraft
    ) {
        return apiRoot
                .types()
                .post(typeDraft)
                .execute();
    }

}
