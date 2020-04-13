package confluent.cloud;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.config.SaslConfigs;

import org.apache.avro.generic.GenericData.Record;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;




public class ConfluentAvroConsumer {
	

	
	public static void main(String[] args) {
		
		String jdbcURL = "jdbc:sqlserver://" + GeneralConfig.sqlServerName + ";databasename=Work;integratedSecurity=true;";
		
		
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
        
        consumer.subscribe(Arrays.asList("Race"));
        
        

        try {
        	Connection conn = DriverManager.getConnection(jdbcURL);
        	
			while (true) {
				ConsumerRecords<String, Record> records = consumer.poll(Duration.ofMillis(1000));
				for (ConsumerRecord<String, Record> record : records) {
					
					Record race = record.value();
					
					PreparedStatement stmt = conn.prepareStatement("INSERT INTO work.dbo.TempRyanKafka VALUES (?,?,?,?,?,?)");
					
					stmt.setInt(1,(int) race.get("Place"));
					stmt.setString(2,race.get("Name").toString());
					stmt.setString(3,race.get("Year").toString());
					stmt.setString(4,race.get("Team").toString());
					stmt.setString(5,race.get("AverageMile").toString());
					stmt.setString(6,race.get("Time").toString());
				
					stmt.execute();
					stmt.close();
					
					System.out.println("Inserted into the database: " + race);

				}
				
			}
			
        } catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			System.out.println(("Exception occured while consuming messages" + e));
		}  finally {
			consumer.close();
		}
        
    }
	
}
