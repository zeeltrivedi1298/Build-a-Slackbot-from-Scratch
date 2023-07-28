# package agebot;

# import com.slack.api.Slack;


# import com.slack.api.app_backend.events.payload.EventsApiPayload;
# import com.slack.api.bolt.context.builtin.EventContext;
# import com.slack.api.bolt.response.Response;
# import com.slack.api.methods.SlackApiException;
# import com.slack.api.methods.request.chat.ChatPostMessageRequest;
# import com.slack.api.model.event.MessageEvent;

# import java.io.IOException;
# import java.time.DateTimeException;
# import java.time.LocalDate;
# import java.time.Period;
# import java.time.format.DateTimeFormatter;
# import java.util.concurrent.ExecutorService;

# public class AgeBot {

#     private final ExecutorService executorService;

#     public AgeBot(ExecutorService executorService) {
#         this.executorService = executorService;
#     }

#     public Response handleHello(EventsApiPayload<MessageEvent> payload, EventContext context) throws SlackApiException, IOException {
#         //Write your code here
#     }
#     //Uncomment the below function when needed
#     // public Response handleAgeMessage(EventsApiPayload<MessageEvent> payload, EventContext context) throws SlackApiException, IOException {
        
#     // }
# }

package agebot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.model.user.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;

public class AgeBot {

    private final ExecutorService executorService;

    public AgeBot(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Response handleAgeMessage(App app, EventContext context, MessageEvent event) throws SlackApiException, IOException {
        String inputDate = event.getText().trim();
        User user = getUserInfo(app, event.getUser());
        if (inputDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            LocalDate dob = LocalDate.parse(inputDate, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate now = LocalDate.now();
            Period age = Period.between(dob, now);
            String message = String.format("%s, your age is %d years.", user.getRealName(), age.getYears());
            ChatPostMessageResponse postResponse = postMessage(app, event.getChannel(), message);
            if (postResponse.isOk()) {
                return Response.ok();
            } else {
                return Response.error(postResponse.getError());
            }
        } else {
            String message = String.format("Sorry %s, I couldn't understand your date format. Please enter your date of birth in YYYY-MM-DD format.", user.getRealName());
            ChatPostMessageResponse postResponse = postMessage(app, event.getChannel(), message);
            if (postResponse.isOk()) {
                return Response.ok();
            } else {
                return Response.error(postResponse.getError());
            }
        }
    }

    private User getUserInfo(App app, String userId) throws SlackApiException {
        UsersInfoResponse response = app.client().usersInfo(
                UsersInfoRequest.builder().user(userId).build());
        return response.getUser();
    }

    private ChatPostMessageResponse postMessage(App app, String channel, String message) throws SlackApiException {
        ChatPostMessageRequest postRequest = ChatPostMessageRequest.builder()
                .channel(channel)
                .text(message)
                .build();
        return app.client().chatPostMessage(postRequest);
    }
}
