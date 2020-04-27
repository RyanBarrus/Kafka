package processes;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;


public class SharedSpecificProducer<T> {
	
	KafkaProducer<String, T> producer;

	public SharedSpecificProducer(String BootstrapServer, String SchemaRegistryURL) {

		Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryURL);

        this.producer = new KafkaProducer<String,T>(props);
        
    }
}
