package faang.school.postservice.messaging.post;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventPublisher {

    private final KafkaTemplate<String, PostEvent> kafkaTemplateForPostEvent;
    private final PostEventMapper mapper;

    @Value("${spring.kafka.topics.post-publication}")
    private String postPublicationTopic;

    public void send(Post post) {
        PostEvent event = mapper.toPostEvent(post);

        CompletableFuture<SendResult<String, PostEvent>> future = kafkaTemplateForPostEvent
                .send(postPublicationTopic, event);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Post event was sent: {}", result);
            } else {
                log.error("Failed to send post event: {}", e.getMessage());
            }
        });
    }
}
