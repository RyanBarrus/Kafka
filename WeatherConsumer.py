from confluent_kafka.avro import AvroConsumer
from confluent_kafka.avro.serializer import SerializerError
import pymongo
from pprint import pprint

# get saved keys
import generalconfig as cfg

mongoDB = cfg.pwd['mongoDB']
confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']
confluentSchemaRegistryKey = cfg.pwd['confluentSchemaRegistryKey']
confluentSchemaRegistrySecret = cfg.pwd['confluentSchemaRegistrySecret']

client = pymongo.MongoClient(
    f"mongodb+srv://ryanbarrus:{mongoDB}@cluster0-wxksn.azure.mongodb.net/test?retryWrites=true&w=majority", ssl=True)
weather = client['Weather']
randomWeather = weather['RandomWeather']

c = AvroConsumer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
                  'security.protocol': 'SASL_SSL',
                  'sasl.mechanism': 'PLAIN',
                  'sasl.username': confluentKey,
                  'sasl.password': confluentSecret,
                  'schema.registry.url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
                  'schema.registry.basic.auth.credentials.source': 'USER_INFO',
                  'schema.registry.basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}',
                  'group.id': '1',
                  })

c.subscribe(['weather'])

while True:
    try:
        msg = c.poll(timeout=20)

    except SerializerError as e:
        print("Message deserialization failed, skipping bad message.")
        continue

    if msg is None:
        break

    weatherUpdate = msg.value()

    randomWeather.insert_one(weatherUpdate)

    print(f'Inserted random weather update into MongoDb')
    pprint(weatherUpdate)
