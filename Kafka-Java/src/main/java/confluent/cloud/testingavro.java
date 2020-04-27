package confluent.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import schemas.race.race;


public class testingavro {

	public static void main(String[] args) {
		
	
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "13.82.6.66:9092");
       
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://13.82.6.66:8081");


        try {

            KafkaProducer<String, race> producer = new KafkaProducer<String,race>(props);
            
			BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/Resources/Race.csv"));
			
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withIgnoreHeaderCase().withTrim());
			 
			 for (CSVRecord csvRecord: csvParser) {
				 
		            int Place = Integer.parseInt(csvRecord.get("Place"));
		            String Name = csvRecord.get("Name");
		            String Year = csvRecord.get("Year");
		            String Team = csvRecord.get("Team");
		            String AverageMile = csvRecord.get("AverageMile");
		            String Time = csvRecord.get("Time");
		            
		            race thisRace = new race(Place,Name,Year,Team,AverageMile,Time);
		            
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
