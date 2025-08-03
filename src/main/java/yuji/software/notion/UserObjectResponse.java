package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(UserObjectResponse.PersonUserObjectResponse.class),
        @JsonSubTypes.Type(UserObjectResponse.BotUserObjectResponse.class),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public sealed interface UserObjectResponse {
    String id();

    String object();

    String name();

    String avatarUrl();

    @JsonTypeName("person")
    record PersonUserObjectResponse(
            String id,
            String object,
            String name,
            String avatarUrl,
            String type,
            Person person
    ) implements UserObjectResponse {
        record Person(String email) {
        }
    }

    @JsonTypeName("bot")
    record BotUserObjectResponse(
            String id,
            String object,
            String name,
            String avatarUrl,
            String type,
            BotInfoResponse bot
    ) implements UserObjectResponse {
        record BotInfoResponse(
                Owner owner,
                String workspaceName,
                WorkspaceLimits workspaceLimits
        ) {
            record Owner(String type, User user) {
                record User(String type, Person person, String name, String avatarUrl, String id, String object) {
                    record Person(String email) {
                    }
                }
            }

            record WorkspaceLimits(int maxFileUploadSizeInBytes) {
            }
        }
    }
}
