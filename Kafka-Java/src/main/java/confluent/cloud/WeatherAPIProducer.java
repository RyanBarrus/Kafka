package confluent.cloud;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.SaslConfigs;
import org.bson.Document;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;

import org.apache.avro.Schema;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig;
import io.confluent.kafka.schemaregistry.client.rest.RestService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherAPIProducer {

	public static void main(String[] args) {
		
		while (true) {
			int CityID = getCityIDFromMongoDB();
			System.out.println("Using CityID: " + CityID);
			
			JsonObject randomWeather = getWeatherFromAPI(CityID);
			System.out.println("Got Weather: " + randomWeather);
			
			randomWeather = WeatherAvroSchemaRenamer(randomWeather);
			System.out.println("Renaming 1h and 3h to one_h and three_h in snow and rain");
			
	        randomWeather = WeatherAvroSchemaTypeSpecifier(randomWeather);
	        System.out.println("Specificed data types for fields with null options");
	        
	        randomWeather = WeatherAvroSchemaDefaulter(randomWeather);
	        System.out.println("Defaulted to null for missing fields");
	        
	        String schemaRegistryUrl = "https://psrc-4r0k9.westus2.azure.confluent.cloud";
			Schema schema = getSchemaFromSchemaRegistry(schemaRegistryUrl);
			System.out.println("Got schema from schema registry: " + schema);
	        
	        ProducerRecord<Object, Object> record = sendWeatherToKafka(randomWeather,schema,schemaRegistryUrl);
			System.out.println("Sent record to Kafka: " + record);
			
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				System.out.println(e);
			}
			
			
		}
		
	}
	
	public static ProducerRecord<Object, Object> sendWeatherToKafka(JsonObject randomWeather,Schema schema,String schemaRegistryUrl) {
		 Properties props = new Properties();
	        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "pkc-41973.westus2.azure.confluent.cloud:9092");
	        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
	        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
	        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" 
	                + GeneralConfig.confluentKey + "\" password=\"" + GeneralConfig.confluentSecret + "\";");
	        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
	        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  "io.confluent.kafka.serializers.KafkaAvroSerializer");
	        
	        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
	        props.put(KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
	        props.put(KafkaAvroSerializerConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
	        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);

	        try {
	        	Decoder decoder = DecoderFactory.get().jsonDecoder(schema, randomWeather.toString());

	        	GenericDatumReader<Record> reader = new GenericDatumReader<Record>(schema);
	        	
	        	Record randomWeatherMessage = reader.read(null, decoder);

	            KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);
	            
	            ProducerRecord<Object, Object> record = new ProducerRecord<>("weather", null, randomWeatherMessage);
	            
	            producer.send(record);
	            producer.close();
	            
	            return(record);

			            
	        } catch (Exception e) {
				e.printStackTrace();
			}
	    return null;
	}
	
	
	public static Schema getSchemaFromSchemaRegistry(String schemaRegistryUrl) {
		RestService rest = new RestService(schemaRegistryUrl);
        Map<String,String> configs = new HashMap<String,String>();
        configs.put(SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE,"USER_INFO");
        configs.put(SchemaRegistryClientConfig.USER_INFO_CONFIG, GeneralConfig.confluentSchemaRegistryKey 
        		+ ":" + GeneralConfig.confluentSchemaRegistrySecret);
        
        rest.configure(configs);

        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(rest,1);
        
        
        try {
        	int LatestID = client.getLatestSchemaMetadata("weather-value").getId();
        	Schema schema = client.getById(LatestID);
        	return(schema);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
		
	}
	
	
	public static int getCityIDFromMongoDB() {
		try {
        	MongoClient client = MongoClients.create("mongodb+srv://ryanbarrus:" + GeneralConfig.mongoDB +
        			"@cluster0-wxksn.azure.mongodb.net/test?retryWrites=true&w=majority");
        	MongoDatabase db = client.getDatabase("Weather");
        	MongoCollection < Document > cities = db.getCollection("Cities");
        	Random rn = new Random();
        	int CityCount = (int) cities.countDocuments();
        	int RandomCity = rn.nextInt(CityCount);
        	MongoCursor<Document> CityContainer = cities.find().limit(1).skip(RandomCity).cursor();
        	int CityID = (int) CityContainer.next().get("_id");
        	
        	return CityID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	public static JsonObject getWeatherFromAPI(int CityID) {
		try {
        	HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://api.openweathermap.org/data/2.5/weather?id=" + CityID 
                    		+ "&APPID=" + GeneralConfig.openWeatherMap))
                    .build();

            HttpResponse<String> response =  httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            
            String responseBody = response.body();

            Gson gson = new GsonBuilder().create();
            JsonObject randomWeather = gson.fromJson(responseBody, JsonObject.class);
            
            return randomWeather;

		} catch (Exception e) {
			System.out.println(("Exception occured while consuming messages" + e));
		}
		
		return null;
		
	}
	
	
	public static JsonObject WeatherAvroSchemaRenamer(JsonObject randomWeather) {
		
		if (randomWeather.has("snow")) {
			if (randomWeather.getAsJsonObject("snow").has("1h")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("float", randomWeather.getAsJsonObject("snow").get("1h"));
				randomWeather.getAsJsonObject("snow").add("one_h", typeSpecific);
				randomWeather.getAsJsonObject("snow").remove("1h");
			}
			if (randomWeather.getAsJsonObject("snow").has("3h")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("float", randomWeather.getAsJsonObject("snow").get("3h"));
				randomWeather.getAsJsonObject("snow").add("three_h", typeSpecific);
				randomWeather.getAsJsonObject("snow").remove("3h");
			}
		}
		if (randomWeather.has("rain")) {
			if (randomWeather.getAsJsonObject("rain").has("1h")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("float", randomWeather.getAsJsonObject("rain").get("1h"));
				randomWeather.getAsJsonObject("rain").add("one_h", typeSpecific);
				randomWeather.getAsJsonObject("rain").remove("1h");
			}
			if (randomWeather.getAsJsonObject("rain").has("3h")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("float", randomWeather.getAsJsonObject("rain").get("3h"));
				randomWeather.getAsJsonObject("rain").add("three_h", typeSpecific);
				randomWeather.getAsJsonObject("rain").remove("3h");
			}
		}
		
		return randomWeather;
	}
	
public static JsonObject WeatherAvroSchemaDefaulter(JsonObject randomWeather) {
	
		JsonParser parser = new JsonParser();
		

		if (randomWeather.has("snow")) {
			if (!randomWeather.getAsJsonObject("snow").has("one_h")) {
				randomWeather.getAsJsonObject("snow").add("one_h", null);
			}
			if (!randomWeather.getAsJsonObject("snow").has("three_h")) {
				randomWeather.getAsJsonObject("snow").add("three_h", null);
			}
		} else {
			randomWeather.add("snow", parser.parse("{\"one_h\":null,\"three_h\":null}"));
		}
		

		if (randomWeather.has("rain")) {
			if (!randomWeather.getAsJsonObject("rain").has("one_h")) {
				randomWeather.getAsJsonObject("rain").add("one_h", null);
			}
			if (!randomWeather.getAsJsonObject("rain").has("three_h")) {
				randomWeather.getAsJsonObject("rain").add("three_h", null);
			}
		} else {
			randomWeather.add("rain", parser.parse("{\"one_h\":null,\"three_h\":null}"));
		}
		
		
		if (randomWeather.has("main")) {
			if (!randomWeather.getAsJsonObject("main").has("sea_level")) {
				randomWeather.getAsJsonObject("main").add("sea_level", null);
			}
			
			if (!randomWeather.getAsJsonObject("main").has("grnd_level")) {
				randomWeather.getAsJsonObject("main").add("grnd_level", null);
			}
		}
		
		if (randomWeather.has("wind")) {
			if (!randomWeather.getAsJsonObject("wind").has("deg")) {
				randomWeather.getAsJsonObject("wind").add("deg", null);
			}
			
		}
		
		
		if (randomWeather.has("sys")) {
			if (!randomWeather.getAsJsonObject("sys").has("id")) {
				randomWeather.getAsJsonObject("sys").add("id", null);
			}
			
			if (!randomWeather.getAsJsonObject("sys").has("message")) {
				randomWeather.getAsJsonObject("sys").add("message", null);
			}
			
			if (!randomWeather.getAsJsonObject("sys").has("type")) {
				randomWeather.getAsJsonObject("sys").add("type", null);
			}
		}
		
		if (!randomWeather.has("visibility")) {
			randomWeather.add("visibility", null);
		}

		return randomWeather;
	}

	public static JsonObject WeatherAvroSchemaTypeSpecifier(JsonObject randomWeather) {
		if (randomWeather.has("visibility")) {
			JsonObject typeSpecific = new JsonObject();
			typeSpecific.add("int", randomWeather.get("visibility"));
			randomWeather.remove("visibility");
			randomWeather.add("visibility",typeSpecific);
		}
		
		if (randomWeather.has("main")) {
			if (randomWeather.getAsJsonObject("main").has("sea_level")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("int", randomWeather.getAsJsonObject("main").get("sea_level"));
				randomWeather.getAsJsonObject("main").remove("sea_level");
				randomWeather.getAsJsonObject("main").add("sea_level",typeSpecific);
			}
			
			if (randomWeather.getAsJsonObject("main").has("grnd_level")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("int", randomWeather.getAsJsonObject("main").get("grnd_level"));
				randomWeather.getAsJsonObject("main").remove("grnd_level");
				randomWeather.getAsJsonObject("main").add("grnd_level",typeSpecific);
			}
			
		}
		
		if (randomWeather.has("wind")) {
			if (randomWeather.getAsJsonObject("wind").has("deg")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("int", randomWeather.getAsJsonObject("wind").get("deg"));
				randomWeather.getAsJsonObject("wind").remove("deg");
				randomWeather.getAsJsonObject("wind").add("deg",typeSpecific);
			}
		}

		if (randomWeather.has("sys")) {
			if (randomWeather.getAsJsonObject("sys").has("type")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("int", randomWeather.getAsJsonObject("sys").get("type"));
				randomWeather.getAsJsonObject("sys").remove("type");
				randomWeather.getAsJsonObject("sys").add("type",typeSpecific);
			}
			
			if (randomWeather.getAsJsonObject("sys").has("id")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("int", randomWeather.getAsJsonObject("sys").get("id"));
				randomWeather.getAsJsonObject("sys").remove("id");
				randomWeather.getAsJsonObject("sys").add("id",typeSpecific);
			}
			
			if (randomWeather.getAsJsonObject("sys").has("message")) {
				JsonObject typeSpecific = new JsonObject();
				typeSpecific.add("float", randomWeather.getAsJsonObject("sys").get("message"));
				randomWeather.getAsJsonObject("sys").remove("message");
				randomWeather.getAsJsonObject("sys").add("message",typeSpecific);
			}
		}
		
		return randomWeather;
	}
}
