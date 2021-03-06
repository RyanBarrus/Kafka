package confluent.cloud;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.avro.generic.GenericData.Record;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.header.Header;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


import org.bson.Document;


public class WeatherConsumer {

	public static void main(String[] args) {
		Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "pkc-41973.westus2.azure.confluent.cloud:9092");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" 
                + GeneralConfig.confluentKey + "\" password=\"" + GeneralConfig.confluentSecret + "\";");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,  "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroup1");
        
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "https://psrc-4r0k9.westus2.azure.confluent.cloud");
        props.put(KafkaAvroDeserializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
        props.put(KafkaAvroDeserializerConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);

        KafkaConsumer<String, Record> consumer = new KafkaConsumer<String, Record>(props);
        
        consumer.subscribe(Arrays.asList("weather"));
        
        
        try {
        	MongoClient client = MongoClients.create("mongodb+srv://ryanbarrus:" + GeneralConfig.mongoDB +
        			"@cluster0-wxksn.azure.mongodb.net/test?retryWrites=true&w=majority");
        	
        	MongoDatabase db = client.getDatabase("Weather");
        	
        	MongoCollection < Document > randomWeather = db.getCollection("RandomWeather");
        	
			while (true) {
				ConsumerRecords<String, Record> records = consumer.poll(Duration.ofMillis(1000));
				for (ConsumerRecord<String, Record> record : records) {
					
					
					for (Header header : record.headers()) {
						System.out.println("here");
						System.out.println(header.toString());
					}
					System.out.println("after");
					
					
					
					Record weather = record.value();
					
					String wString = weather.toString();
					
					System.out.println(wString);
					
					Document weatherDocument = Document.parse( wString );

			        randomWeather.insertOne(weatherDocument);
					System.out.println("Inserted into MongoDB: " + weatherDocument);

				}
				
				
			}
			
		} catch (Exception e) {
			System.out.println(("Exception occured while consuming messages" + e));
		}  finally {
			consumer.close();

		}
        
        
		
	}
	
}
