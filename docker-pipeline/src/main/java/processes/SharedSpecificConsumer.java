package processes;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class SharedSpecificConsumer<T> {
	
	 KafkaConsumer<String, T> consumer;

	 public SharedSpecificConsumer(String BootstrapServer, String SchemaRegistryURL,String GroupID) {

		 Properties props = new Properties();
	        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BootstrapServer);
	        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
	        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,  "io.confluent.kafka.serializers.KafkaAvroDeserializer");
	        props.put(ConsumerConfig.GROUP_ID_CONFIG, GroupID);
	        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryURL);
	        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

	        this.consumer = new KafkaConsumer<String, T>(props);

	 }
	 
	 public void subscribeAndHandleRebalance(String topic) {
		 this.consumer.subscribe(Arrays.asList(topic), new HandleRebalance());
	 }
	 
	private HashMap<TopicPartition, OffsetAndMetadata> currentOffsets =  new HashMap<>();
	
	private class HandleRebalance implements ConsumerRebalanceListener {    
		
		public void onPartitionsAssigned(Collection<TopicPartition> partitions) {     
			
		}
		
		public void onPartitionsRevoked(Collection<TopicPartition> partitions) {        
		System.out.println("Lost partitions in rebalance. Committing current offsets:" + currentOffsets);        
		consumer.commitSync(currentOffsets);     
		} 
	
	}
	

}
