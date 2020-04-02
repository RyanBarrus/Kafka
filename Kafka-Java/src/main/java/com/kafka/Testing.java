package com.kafka;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig;
import io.confluent.kafka.schemaregistry.client.rest.RestService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Testing {

	public static void main(String[] args) {
		JsonObject randomWeather = getWeatherFromAPI();
		sendWeatherToKafka(randomWeather);
		
		System.out.println(randomWeather);
	}
	
	public static void sendWeatherToKafka(JsonObject randomWeather) {
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
	        	int LatestID = client.getLatestSchemaMetadata("weather-value").getId();
	        	Schema schema = client.getById(LatestID);
	        	
	        	GenericRecord avroRecord = new GenericData.Record(schema);
	        	

	        	avroRecord.put("base", randomWeather.get("base").toString());
	        	avroRecord.put("clouds", randomWeather.get("clouds").toString());
	        	avroRecord.put("cod", randomWeather.get("cod"));
	        	avroRecord.put("coord", randomWeather.get("coord"));
	        	avroRecord.put("snow", randomWeather.get("snow"));
	        	avroRecord.put("rain", randomWeather.get("rain"));
	        	avroRecord.put("dt", randomWeather.get("dt"));
	        	avroRecord.put("id", randomWeather.get("id"));
	        	avroRecord.put("main", randomWeather.get("main"));
	        	avroRecord.put("name", randomWeather.get("name"));
	        	avroRecord.put("timezone", randomWeather.get("timezone"));
	        	avroRecord.put("visibility", randomWeather.get("visibility"));
	        	avroRecord.put("weather", randomWeather.get("weather"));
	        	avroRecord.put("wind", randomWeather.get("wind"));
	        	
	        	System.out.println(avroRecord);
	        	
	            KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);
	            
	            System.out.println("about to create record");
	            ProducerRecord<Object, Object> record = new ProducerRecord<>("weather", null, avroRecord);
	            System.out.println("created record");
	            System.out.println(record);
	            
	            System.out.println("about to send");
	            producer.send(record);
	            System.out.println("sent");
	            producer.close();
	            
			            
	        } catch (AvroTypeException e) {
	        	e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	public static JsonObject getWeatherFromAPI() {
		
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
            
            
            randomWeather = WeatherAvroSchemaRenamer(randomWeather);
           
            
            return randomWeather;

		} catch (Exception e) {
			System.out.println(("Exception occured while consuming messages" + e));
		}
		
		return null;
		
	}
	
	
	public static JsonObject WeatherAvroSchemaRenamer(JsonObject randomWeather) {
		
		JsonParser parser = new JsonParser();
		
		
		if (randomWeather.has("snow")) {
			if (randomWeather.getAsJsonObject("snow").has("1h")) {
				randomWeather.getAsJsonObject("snow").add("one_h", randomWeather.getAsJsonObject("snow").get("1h"));
				randomWeather.getAsJsonObject("snow").remove("1h");
			}
			if (randomWeather.getAsJsonObject("snow").has("3h")) {
				randomWeather.getAsJsonObject("snow").add("three_h", randomWeather.getAsJsonObject("snow").get("3h"));
				randomWeather.getAsJsonObject("snow").remove("3h");
			}
		} else {
			randomWeather.add("snow", parser.parse("{\"one_h\":null,\"three_h\":null}"));
		}
		if (randomWeather.has("rain")) {
			if (randomWeather.getAsJsonObject("rain").has("1h")) {
				randomWeather.getAsJsonObject("rain").add("one_h", randomWeather.getAsJsonObject("rain").get("1h"));
				randomWeather.getAsJsonObject("rain").remove("1h");
			}
			if (randomWeather.getAsJsonObject("rain").has("3h")) {
				randomWeather.getAsJsonObject("rain").add("three_h", randomWeather.getAsJsonObject("rain").get("3h"));
				randomWeather.getAsJsonObject("rain").remove("3h");
			}
		} else {
			randomWeather.add("rain", parser.parse("{\"one_h\":null,\"three_h\":null}"));
		}
		

		return randomWeather;
	}
	

}
