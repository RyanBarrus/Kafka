package com.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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

public class RawStream {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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

        
        
        StreamsBuilder builder = new StreamsBuilder();
        
        KStream<String, String> testing = builder.stream("Training");
        
        testing
        	.map((key,value) -> new KeyValue<String,String>(value,value))
		    .groupByKey()
		    .count()
		    .mapValues(value -> Long.toString(value))
		    .toStream()
//		    .print(Printed.toSysOut());
		    .to("StreamResults", Produced.with(Serdes.String(), Serdes.String()));
//        	.mapValues(race -> race.get("Team").toString());
//	        .selectKey((IgnoredKey,race) -> race.get("Team").toString())
//	        .groupByKey(Grouped.with(
//	        	      Serdes.String(), /* key */
//	        	      raceAvroSerde)     /* value */
//	        	  )
//	        .count();
//        

       
//        leaderboard.toStream().to("StreamResults",Produced.with(Serdes.String(),Serdes.Long()));
        
        Topology topology = builder.build();
        
//        System.out.println(topology.describe());
        
        KafkaStreams streams = new KafkaStreams(topology, props);
        
        streams.start();
        

        
	}
	

}
