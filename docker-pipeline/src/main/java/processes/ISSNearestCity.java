package processes;


import java.time.Duration;
import java.util.Scanner;
import java.sql.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;

import schemas.ISS.ISS;
import schemas.NearestCity.NearestCity;

public class ISSNearestCity {
	
	private Thread t1;
	private Thread t2; 
	Scanner in;
	volatile String silencer = "verbose";
	

	public ISSNearestCity(Scanner in) {
		this.in = in;
		t1 = new Thread(new executer());
		t2 = new Thread(new silenceListener());
		t1.start();    
		t2.start();
	}

	private class executer implements Runnable {
		public void run() {
			
			SharedSpecificConsumer<ISS> issConsumer =  new SharedSpecificConsumer<ISS>("13.82.6.66:9092","http://13.82.6.66:8081","ISS");
			SharedSpecificProducer<NearestCity> nearestProducer = new SharedSpecificProducer<NearestCity>("13.82.6.66:9092","http://13.82.6.66:8081");
			mysql mysql = new mysql("jdbc:mysql://13.82.6.66:3306");

			issConsumer.subscribeAndHandleRebalance("ISS");
			while (true) {
				ConsumerRecords<String, ISS> records = issConsumer.consumer.poll(Duration.ofMillis(1000));
				for (ConsumerRecord<String, ISS> record : records) {
					
					ISS currentISS = record.value();
					
					if (silencer.equals("verbose")) System.out.println("got record from kafka" + currentISS.toString());
					
					ResultSet nearestMysqlRecord = mysql.nearestByEuclidian(currentISS.getLatitude(), currentISS.getLongitude());
					
					try {
						nearestMysqlRecord.next();
						NearestCity nearestCity = new NearestCity(
								nearestMysqlRecord.getLong("ID"),
								nearestMysqlRecord.getString("name"),
								nearestMysqlRecord.getString("country"),
								nearestMysqlRecord.getFloat("latitude"),
								nearestMysqlRecord.getFloat("longitude"),
								nearestMysqlRecord.getFloat("EuclidianDistance")
								);
						
						ProducerRecord<String, NearestCity> nearestRecord = new ProducerRecord<String, NearestCity>("NearestCity", null, nearestCity);
			            
						nearestProducer.producer.send(nearestRecord);
						
						if (silencer.equals("verbose")) System.out.println("sent record to kafka" + nearestRecord.toString());
						
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

	
	
	
	static class mysql {
	    Connection conn;
	    
		public mysql (String url) {
			try {
				Connection conn = DriverManager.getConnection(url,"root","TrainKafka1!");
				this.conn = conn;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public ResultSet nearestByEuclidian(float latitude, float longitude) {
			try {
				
				PreparedStatement stmt = this.conn.prepareStatement("CALL weather.nearestCityByEuclidian(?,?)");
				
				stmt.setFloat(1,latitude);
				stmt.setFloat(2,longitude);
				
				ResultSet nearestCity = stmt.executeQuery();
				
				return nearestCity;
				
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

		}
		
	}

}


