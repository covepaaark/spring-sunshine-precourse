package study;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class JokeController {
    ChatClient client;

    public JokeController(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    @GetMapping("/v1/joke")
    public String jokev1(
            @RequestParam(defaultValue = "Tell me a joke") String message,
            @RequestParam(defaultValue = "Programming") String topic
    ) {
        var template = new PromptTemplate("Tell me a joke about {topic}");
        var prompt = template.render(Map.of("topic", topic));
        return client.prompt(prompt)
                .call()
                .content()
                ;
    }

    @GetMapping("/v2/joke")
    public String jokev2(
            @RequestParam(defaultValue = "Tell me a joke") String message,
            @RequestParam(defaultValue = "Programming") String topic
    ) {
        var template = new PromptTemplate("Tell me a joke about {topic}");
        var prompt = template.render(Map.of("topic", topic));
        return client.prompt(prompt)
                .call()
                .content()
                ;
    }

    @GetMapping("/v3/joke")
    public ChatResponse jokev3(
            @RequestParam(defaultValue = "Tell me a joke") String message,
            @RequestParam(defaultValue = "Programming") String topic
    ) {
        var template = new PromptTemplate("Tell me a joke about {topic}");
        var prompt = template.render(Map.of("topic", topic));
        return client.prompt(prompt)
                .call()
                .chatResponse()
                ;
    }


    @GetMapping("/v4/joke")
    public ChatResponse jokev4(
            @RequestParam(defaultValue = "cove") String name,
            @RequestParam(defaultValue = "gentle") String voice
    ) {
        var user = new UserMessage("""
            Tell me about three famous pirates from the Golden Age of Piracy and what they did.
            Write at least one sentence for each pirate.
            """
        );
        var template = new SystemPromptTemplate("""
            You are a helpful AI assistant.
            You are an AI assistant that helps people find information.
            Your name is {name}.
            You should reply to the user's request using your name and in the style of a {voice}.
            """
        );
        var system = template.createMessage(Map.of("name", name, "voice", voice));
        var prompt = new Prompt(user, system);
        return client.prompt(prompt).call().chatResponse();
    }

//    @GetMapping
//    public ActorFilms actors(
//
//    ) {
//        var beanOutputConverter = new BeanOutputConverter<>(ActorsFilms.class);
//        var format = beanOutputConverter.getFormat();
//        var userMessage = """
//            Generate the filmography of 5 movies for {actor}.
//            {format}
//            """;
//        var promptTemplate = new PromptTemplate(userMessage, Map.of("actor", actor, "format", format));
//        var prompt = promptTemplate.create();
//        beanOutputConverter.convert(chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText());
//
//    }

    @GetMapping("/v1/addDays")
    public String addDays (
            @RequestParam(defaultValue = "2023-01-01") String days
    ){
        var template = new PromptTemplate("Add {days} days to {startDate}");
        var prompt = template.render(Map.of("days", 10, "startDate", days));
        return client.prompt(prompt)
            .tools(new Functions())
            .call()
            .content();

    }
}




