package config;

import config.properties.KafkaSenderProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.cloud.sleuth.zipkin2.ZipkinAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sender.KafkaReporter;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

@Configuration
@EnableConfigurationProperties(KafkaSenderProperties.class)
@AutoConfigureBefore(TraceAutoConfiguration.class) // TODO check for truthiness
public class SleuthSpanSenderConfig {

  @Bean(ZipkinAutoConfiguration.REPORTER_BEAN_NAME)
  Reporter<Span> myReporter(KafkaSenderProperties kafkaSenderProperties) {
    return new KafkaReporter(kafkaSenderProperties);
  }
}
