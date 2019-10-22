package cn.kafka.demo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @BelongsProject: MavenProjectDemo
 * @BelongsPackage: cn.kafka.demo
 * @Author: lhb
 * @CreateTime: 2019-10-15 17:48
 * @Description:
 */
public class TestKafkaProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.16.133:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);

        ProducerRecord<String, String> data = new ProducerRecord<>("test", "1016");
        producer.send(data);

        producer.close();
    }
}
