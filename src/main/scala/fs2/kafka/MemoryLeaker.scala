package fs2.kafka

import cats.effect.{IO, IOApp, Resource}
import cats.syntax.all._

import scala.concurrent.duration._

object MemoryLeaker extends IOApp.Simple {
  private val topic: String = "source"

  override def run: IO[Unit] =
    (consumer, producer).tupled.use { case (consumer, producer) =>
      // Subscribe to the topic
      consumer.subscribeTo(topic) >>
        // Produce a record to the topic
        producer.produce(ProducerRecords.one(ProducerRecord(topic, Some("key"), Some("value")))) >>
        // For each record consumed, we produce it back to the topic, hence creating an infinite loop
        consumer.stream
          .debug()
          .evalMap { record =>
            IO.sleep(10.millis) >>
              producer
                .produce(ProducerRecords.one(ProducerRecord(topic, record.record.key, record.record.value)))
                .flatten >> record.offset.commit
          }
          .compile
          .drain
    }

  private val consumer: Resource[IO, KafkaConsumer[IO, Option[String], Option[String]]] =
    KafkaConsumer[IO].resource(
      ConsumerSettings[IO, Option[String], Option[String]]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers("localhost:9094")
        .withGroupId("group")
    )

  private val producer: Resource[IO, KafkaProducer[IO, Option[String], Option[String]]] =
    KafkaProducer[IO].resource(
      ProducerSettings[IO, Option[String], Option[String]]
        .withBootstrapServers("localhost:9094")
    )
}
