package com.bootcamp.service;

import com.bootcamp.dto.Employee;
import com.bootcamp.util.Mapper;
import com.bootcamp.util.Validation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Service
@Slf4j
public class KafkaService {

    @Autowired
    private ReceiverOptions<Integer, String> receiverOptions;

    @Autowired
    private KafkaSender<Integer, String> kafkaSender;

    @Value("${kafka.app.topic}")
    String topicName;

    @Value("${kafka.employee.topic}")
    String empTopic;

    @Value("${kafka.dlq.topic}")
    String dlqTopic;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");

    @PostConstruct
    public void consumeMessage() {
        ReceiverOptions<Integer, String> options = receiverOptions.subscription(Collections.singleton(topicName))
                .addAssignListener(partitions -> log.debug("PartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("PartitionsRevoked {}", partitions));
        Flux<ReceiverRecord<Integer, String>> kafkaFlux = KafkaReceiver.create(options).receive();
        kafkaFlux.subscribe(record -> {
            ReceiverOffset offset = record.receiverOffset();
            offset.acknowledge();
            log.debug("Employee producer message received in consumer => " + dateFormat.format(new Date(record.timestamp())));
            String value = record.value();
            log.debug("Producer value received in consumer => " + value);
            Employee emp = Mapper.getEmpObject(value);
            boolean isValid = Validation.isEmpValid(emp);
            if(isValid) produceValidMsg(emp);
            else produceInValidMsg(emp);
        });
    }

    public void produceValidMsg(Employee emp) {
        SenderRecord<Integer, String, Integer> senderRecord =
                SenderRecord.create(new ProducerRecord<>(empTopic, emp.getId(), Mapper.mapEmpToString(emp)), emp.getId());
        kafkaSender.send(Mono.just(senderRecord))
                .doOnError(e -> log.error("Error occurred in producing kafka message => " + e.getMessage()))
                .doOnNext(e -> log.info("Valid Kafka message creation successful => " + e.recordMetadata()))
                .subscribe();
    }

    public void produceInValidMsg(Employee emp) {
        SenderRecord<Integer, String, Integer> senderRecord =
                SenderRecord.create(new ProducerRecord<>(dlqTopic, emp.getId(), Mapper.mapEmpToString(emp)), emp.getId());
        kafkaSender.send(Mono.just(senderRecord))
                .doOnError(e -> log.error("Error occurred in producing kafka message => " + e.getMessage()))
                .doOnNext(e -> log.info("Invalid Kafka message creation successful => " + e.recordMetadata()))
                .subscribe();
    }

}
