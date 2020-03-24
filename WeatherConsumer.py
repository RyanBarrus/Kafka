from confluent_kafka.avro import AvroConsumer
from confluent_kafka.avro import CachedSchemaRegistryClient
from confluent_kafka.avro.serializer import SerializerError
import pymongo
from pprint import pprint

client = pymongo.MongoClient("mongodb+srv://ryanbarrus:UOMmAxzeGiNCv65l@cluster0-wxksn.azure.mongodb.net/test?retryWrites=true&w=majority",ssl=True)
weather = client['Weather']
randomWeather = weather['RandomWeather']

c = AvroConsumer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
        'security.protocol' : 'SASL_SSL',
        'sasl.mechanism' : 'PLAIN',
        'sasl.username' : "TC63BVUZ2QBK53MJ",
        'sasl.password' : "o6QLpc2EYD9qHyxVDFuytoOVZg7RROkrSwKly7DEQz+nK3HrJrytY1p6btO8gsub",
        'schema.registry.url' : 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
        'schema.registry.basic.auth.credentials.source' : 'USER_INFO',
        'schema.registry.basic.auth.user.info' : 'A3SEGZ3CFXOXB27S:5Z8K+cJ61/JpIjeYf+smXiT21t8nlYCqZ6KaLHUeJnq8cxNHDchBxKH5vFnbQ6cR',
        'group.id': '1',
        })

c.subscribe(['weather'])

while True:
    try :
        msg = c.poll(timeout = 20)

    except SerializerError as e:
        print("Message deserialization failed, skipping bad message.")
        continue

    if msg is None :
        break

    weatherUpdate = msg.value()

    randomWeather.insert_one(weatherUpdate)

    print(f'Inserted random weather update into MongoDb')
    pprint(weatherUpdate)