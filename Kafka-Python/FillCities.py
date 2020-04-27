from requests import get
from pprint import pprint
from time import sleep
from confluent_kafka.avro import AvroProducer
from confluent_kafka.avro import CachedSchemaRegistryClient


client = CachedSchemaRegistryClient({'url': 'http://13.82.6.66:8081'})

SavedSchema = client.get_latest_schema('ISS-value')[1]

p = AvroProducer({'bootstrap.servers': "13.82.6.66:9092",
                  'schema.registry.url': 'http://13.82.6.66:8081',
                  }, default_value_schema=SavedSchema)

while True:
    r = get("http://api.open-notify.org/iss-now.json")
    ISS = r.json()

    message = {'timestamp': ISS['timestamp'],
               'latitude': float(ISS['iss_position']['latitude']),
               'longitude': float(ISS['iss_position']['longitude'])
               }
    pprint(message)

    try:
        p.produce(topic='ISS', value=message, partition=0)
        p.flush()

    except Exception as e:
        print(e)

    sleep(1)










