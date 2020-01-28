package sender;

import config.properties.KafkaSenderProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import zipkin2.Call;
import zipkin2.codec.Encoding;
import zipkin2.reporter.BytesMessageEncoder;
import zipkin2.reporter.Sender;

import java.util.List;
import java.util.Properties;

public class KafkaSender extends Sender {

  private KafkaSenderProperties kafkaSenderProperties;
  private KafkaProducer<Long, String> kafkaProducer;
  private BytesMessageEncoder encoder;

  public KafkaSender(KafkaSenderProperties kafkaSenderProperties) {
    this.kafkaSenderProperties = kafkaSenderProperties;
    this.encoder = BytesMessageEncoder.forEncoding(kafkaSenderProperties.getEncoding());
  }

  public Encoding encoding() {
    return kafkaSenderProperties.getEncoding();
  }

  public int messageMaxBytes() {
    return kafkaSenderProperties.getMessageMaxBytes();
  }

  public int messageSizeInBytes(List<byte[]> list) {
    return encoding().listSizeInBytes(list);
  }

  public Call<Void> sendSpans(List<byte[]> list) {
    byte[] encodedMessage = encoder.encode(list);
    getProducer().send(new ProducerRecord(kafkaSenderProperties.getTopic(), encodedMessage));

    return null;
  }

  private KafkaProducer<Long, String> getProducer() {
    if (kafkaProducer == null) {
      synchronized (this) {
        Properties properties = new Properties();

        properties.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaSenderProperties.getBrokers());

        properties.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        this.kafkaProducer = new KafkaProducer<Long, String>(properties);
      }
    }

    return kafkaProducer;
  }
}
