package confluent.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.SaslConfigs;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import schemas.race.race;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class ConfluentAvroSpecificProducer {

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

        try {

            KafkaProducer<String, race> producer = new KafkaProducer<String,race>(props);
            
			BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/Resources/Race.csv"));
			
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim());
			 
			 for (CSVRecord csvRecord: csvParser) {
				 
				 	race thisRace = new race();
				 	
		            int Place = Integer.parseInt(csvRecord.get("Place"));
		            String Name = csvRecord.get("Name");
		            String Year = csvRecord.get("Year");
		            String Team = csvRecord.get("Team");
		            String AverageMile = csvRecord.get("AverageMile");
		            String Time = csvRecord.get("Time");
		            
		            thisRace.setPlace(Place);
		            thisRace.setName(Name);
		            thisRace.setYear(Year);
		            thisRace.setTeam(Team);
		            thisRace.setAverageMile(AverageMile);
		            thisRace.setTime(Time);
		            
		            ProducerRecord<String, race> record = new ProducerRecord<>("Race", null, thisRace);
		            
		            producer.send(record);
		            
		            
		            System.out.println("Sent record to Kafka: " + record.value());
 
		       }
			 
			 csvParser.close();
			 producer.flush();
			 producer.close();
			 

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        
	}

}
