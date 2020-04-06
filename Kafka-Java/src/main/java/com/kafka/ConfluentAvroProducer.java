package com.kafka;



import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.Schema;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig;
import io.confluent.kafka.schemaregistry.client.rest.RestService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;




public class ConfluentAvroProducer {

	public static void main(String[] args) {
		
		

        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "pkc-41973.westus2.azure.confluent.cloud:9092");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" 
                + GeneralConfig.confluentKey + "\" password=\"" + GeneralConfig.confluentSecret + "\";");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  "io.confluent.kafka.serializers.KafkaAvroSerializer");
        
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "https://psrc-4r0k9.westus2.azure.confluent.cloud");
        props.put(KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
        props.put(KafkaAvroSerializerConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);


        String schemaRegistryUrl = "https://psrc-4r0k9.westus2.azure.confluent.cloud";
        RestService rest = new RestService(schemaRegistryUrl);
        Map<String,String> configs = new HashMap<String,String>();
        
        configs.put(SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
        configs.put(SchemaRegistryClientConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);
        
        rest.configure(configs);

        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(rest,1);
        
        
        try {
        	int LatestID = client.getLatestSchemaMetadata("Race-value").getId();
        	
        	System.out.println(LatestID);
        	Schema schema = client.getById(LatestID);
        	
        	
        	GenericRecord avroRecord = new GenericData.Record(schema);
        	
            KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);
            
			BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/Resources/Race.csv"));
			
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim());
			 
			 for (CSVRecord csvRecord: csvParser) {
				 
		            int Place = Integer.parseInt(csvRecord.get("Place"));
		            String Name = csvRecord.get("Name");
		            String Year = csvRecord.get("Year");
		            String Team = csvRecord.get("Team");
		            String AverageMile = csvRecord.get("AverageMile");
		            String Time = csvRecord.get("Time");
		            
		            avroRecord.put("Place",Place);
		            avroRecord.put("Name",Name);
		            avroRecord.put("Year",Year);
		            avroRecord.put("Team",Team);
		            avroRecord.put("AverageMile",AverageMile);
		            avroRecord.put("Time",Time);
		            

		            ProducerRecord<Object, Object> record = new ProducerRecord<>("Race", null, avroRecord);
		            
		            producer.send(record);
		            
		            System.out.println("Sent record to Kafka: " + record.value());
 
		       }
			 
			 csvParser.close();
			 producer.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (RestClientException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}

}
