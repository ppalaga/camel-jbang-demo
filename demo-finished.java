// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;

public class demo extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("kamelet:chuck-norris-source?period=2000")
            .log("Sending to Artemis: ${body}")
            .to("amqp:queue:jokes");

        from("amqp:queue:jokes")
            .choice()
                .when(simple("${body.startsWith('Chuck')}"))
                    .log("Forwarding from Artemis to kafka:chuck: ${body}")
                    .to("kafka:chuck")
                .otherwise()
                    .log("Forwarding from Artemis to kafka:other: ${body}")
                    .to("kafka:other");

        from("kafka:chuck")
            .log("Received from kafka:chuck: ${body}");

        from("kafka:other")
            .log("Received from kafka:other: ${body}");

    }
}
