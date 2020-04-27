package processes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import schemas.NearestCity.NearestCity;
import schemas.weather.rain;
import schemas.weather.snow;
import schemas.weather.weather;

public class WeatherAtCity {
	
	private Thread t1;
	private Thread t2; 
	Scanner in;
	volatile String silencer = "verbose";
	
	public WeatherAtCity(Scanner in) {
		this.in = in;
		t1 = new Thread(new executer());
		t2 = new Thread(new silenceListener());
		t1.start();    
		t2.start();
	}
	
	
	private class executer implements Runnable  {
		public void run() {
			SharedSpecificConsumer<NearestCity> nearestConsumer =  new SharedSpecificConsumer<NearestCity>("13.82.6.66:9092","http://13.82.6.66:8081","NearestCity");
			SharedSpecificProducer<weather> weatherProducer = new SharedSpecificProducer<weather>("13.82.6.66:9092","http://13.82.6.66:8081");
			
			nearestConsumer.subscribeAndHandleRebalance("NearestCity");
			Long lastCityID = 0L;
			while (true) {
				ConsumerRecords<String, NearestCity> records = nearestConsumer.consumer.poll(Duration.ofMillis(1000));
				for (ConsumerRecord<String, NearestCity> record : records) {
					
					try {
						
						NearestCity currentNearest = record.value();
						if (silencer.equals("verbose")) System.out.println("got record from kafka" + currentNearest.toString());
						
						if (lastCityID == currentNearest.getId()) {
							if (silencer.equals("verbose")) System.out.println("Same city as before and weather is only updated every 10 minutes, so skipping.");
							continue;
						} 
						lastCityID = currentNearest.getId();
						
						
						JsonObject JSONWeather = getWeatherFromAPI((int) currentNearest.getId());
						JSONWeather = jsonWeatherRenamer(JSONWeather);
						ObjectMapper jsonMapper = new ObjectMapper().addMixIn( SpecificRecordBase.class, AvroJsonMixin.class );
						weather currentWeather = jsonMapper.readValue(JSONWeather.toString(), weather.class);
						weather defaultedWeather = weatherDefaulter(currentWeather);
						
						if (silencer.equals("verbose")) System.out.println("got weather from API" + defaultedWeather.toString());
						
						ProducerRecord<String, weather> weatherRecord = new ProducerRecord<String, weather>("WeatherAtCity", null, defaultedWeather);
						weatherProducer.producer.send(weatherRecord);
						if (silencer.equals("verbose")) System.out.println("sent record to kafka" + weatherRecord.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}
		}
	}
	
	
	private class silenceListener implements Runnable {
		public void run(){
			while (true) {
				if (in.hasNext()) {
					silencer = in.next();
					if (!silencer.equals("verbose")) {
			        	System.out.println("Turning off verbose mode");
			        } else {
			        	System.out.println("Turning on verbose mode");
			        }
				}
			}
		}
	}
	
	
	public static JsonObject getWeatherFromAPI(int CityID) {
		try {
        	HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://api.openweathermap.org/data/2.5/weather?id=" + CityID 
                    		+ "&APPID=88873cfc062865970a1fbe01a1e83050"))
                    .build();

            HttpResponse<String> response =  httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            
            String responseBody = response.body();

            Gson gson = new GsonBuilder().create();
            JsonObject issWeather = gson.fromJson(responseBody, JsonObject.class);
            
            return issWeather;

		} catch (Exception e) {
			System.out.println(("Exception occured while consuming messages" + e));
		}
		
		return null;
		
	}
	
	
public static JsonObject jsonWeatherRenamer(JsonObject JSONWeather) { 
	
	String[] parents = new String[]{"snow","rain"};
	String[][] oldNews = new String[][] {
		    {"1h", "one_h"},
		    {"3h", "three_h"}
		    };

	for (String parent : parents) {
		for (String[] oldNew : oldNews) {
			if (JSONWeather.has(parent)) {
				if (JSONWeather.getAsJsonObject(parent).has(oldNew[0])) {
					JsonObject typeSpecific = new JsonObject();
					typeSpecific.add("float", JSONWeather.getAsJsonObject(parent).get(oldNew[0]));
					JSONWeather.getAsJsonObject(parent).add(oldNew[1], typeSpecific);
					JSONWeather.getAsJsonObject(parent).remove(oldNew[0]);
				}
			}
		}	
	}
	
	return JSONWeather;
}
	

public static weather weatherDefaulter(weather currentWeather) {
	
		if (currentWeather.getSnow() == null) {
			currentWeather.setSnow(new snow());
		}
		
		if (currentWeather.getRain() == null) {
			currentWeather.setRain(new rain());
		}
		
		return currentWeather;
	}


/**
* This is used as a mixin to Jackson to allow conversion of Avro type to json and back.
*/
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class AvroJsonMixin
{
   /**
    * Ignore the Avro schema property.
    */
   @JsonIgnore
   abstract Schema getSchema();
   /**
    * Ignore the specific data property.
    */
   @JsonIgnore
   abstract SpecificData getSpecificData();
}

}
