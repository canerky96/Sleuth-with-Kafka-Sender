package sender;

import config.properties.KafkaSenderProperties;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KafkaReporter extends AsyncReporter<Span> {

  private KafkaSender sender;
  private AsyncReporter reporter;

  public KafkaReporter(KafkaSenderProperties kafkaSenderProperties) {
    this.sender = new KafkaSender(kafkaSenderProperties);
    this.reporter =
        AsyncReporter.builder(this.sender)
            .messageTimeout(kafkaSenderProperties.getMessageTimeout(), TimeUnit.SECONDS)
            .queuedMaxSpans(1000)
            .build();
  }

  public void flush() {
    this.reporter.flush();
  }

  public void close() {
    this.reporter.close();
    try {
      this.sender.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void report(Span span) {
    this.reporter.report(span);
  }
}
