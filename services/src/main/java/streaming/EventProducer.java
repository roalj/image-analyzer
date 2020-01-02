package streaming;

import com.kumuluz.ee.streaming.common.annotations.StreamProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@ApplicationScoped
public class EventProducer {

    private static final Logger log = Logger.getLogger(EventProducer.class.getName());

    private static final String TOPIC_NAME = "wb3hbo45-image-analysis";

    @Inject
    @StreamProducer
    private Producer producer;

    public Response produceMessage(String imageId, String imageLocation) {

        JSONObject obj = new JSONObject();
        obj.put("imageId", imageId);
        obj.put("imageLocation", imageLocation);
        obj.put("status", "unprocessed");

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, imageId, obj.toString());

        producer.send(record,
                (metadata, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        log.info("The offset of the produced message record is: " + metadata.offset());
                    }
                });

        return Response.ok().build();

    }
}
