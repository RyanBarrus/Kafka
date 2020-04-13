package confluent.cloud;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;

public class RaceStream {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String schemaRegistryUrl = "https://psrc-4r0k9.westus2.azure.confluent.cloud";
		Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "TestingStreams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-41973.westus2.azure.confluent.cloud:9092");
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" 
                + GeneralConfig.confluentKey + "\" password=\"" + GeneralConfig.confluentSecret + "\";");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG,"3");
        
        
        
//        RestService rest = new RestService(schemaRegistryUrl);
//        Map<String,String> configs = new HashMap<String,String>();
//        configs.put(SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
//        configs.put(SchemaRegistryClientConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
//        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);
//        rest.configure(configs);
//        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(rest,1);
        
        
        GenericAvroSerde raceAvroSerde = new GenericAvroSerde();
  

        final HashMap<String, String> streamConfigs = new HashMap<String,String>();
        
        streamConfigs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,schemaRegistryUrl);
        streamConfigs.put(AbstractKafkaAvroSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
        streamConfigs.put(AbstractKafkaAvroSerDeConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);
        
        
        raceAvroSerde.configure(streamConfigs,false);
        

        StreamsBuilder builder = new StreamsBuilder();
        
        KStream<String, GenericRecord> testing = builder.stream("Race",Consumed.with(Serdes.String(), raceAvroSerde));
        
        testing
		    .map( (key, value) -> KeyValue.pair(value.get("Team").toString(),value.get("Team").toString()))
		    .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
		    .count()
		    .mapValues(value -> value.toString())
		    .toStream()
		    .print(Printed.toSysOut());
//		    .to("StreamResults", Produced.with(Serdes.String(), Serdes.String()));

        Topology topology = builder.build();
        
//        System.out.println(topology.describe());
        
        KafkaStreams streams = new KafkaStreams(topology, props);
        
        streams.start();

        
	}
}

//https://chrzaszcz.dev/2019/08/kafka-streams/
//https://kafka-tutorials.confluent.io/changing-serialization-format/kstreams.html
//https://docs.confluent.io/current/streams/developer-guide/dsl-api.html
//https://aseigneurin.github.io/2018/08/06/kafka-tutorial-7-kafka-streams-serdes-and-avro.html
