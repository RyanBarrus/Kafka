import pymongo
import requests
from random import randrange
from confluent_kafka.avro import AvroProducer
from confluent_kafka.avro import CachedSchemaRegistryClient
from pprint import pprint

#get a random city to work with from MongoDB
client = pymongo.MongoClient("mongodb+srv://ryanbarrus:UOMmAxzeGiNCv65l@cluster0-wxksn.azure.mongodb.net/test?retryWrites=true&w=majority",ssl=True)
weather = client['Weather']
cities = weather['Cities']
CityCount = cities.count_documents({}) - 1
RandomCity = randrange(0,CityCount)
CityID = cities.find().limit(1).skip(RandomCity)[0]['_id']


#call the weather api to
r = requests.get(f'http://api.openweathermap.org/data/2.5/weather?id={CityID}&APPID=88873cfc062865970a1fbe01a1e83050')
randomWeather = r.json()
# pprint(r.json())

client = CachedSchemaRegistryClient({
        'url' : 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
        'basic.auth.credentials.source' : 'USER_INFO',
        'basic.auth.user.info' : 'A3SEGZ3CFXOXB27S:5Z8K+cJ61/JpIjeYf+smXiT21t8nlYCqZ6KaLHUeJnq8cxNHDchBxKH5vFnbQ6cR'
        })

SavedSchema = client.get_latest_schema('weather-value')[1]

p = AvroProducer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
        'security.protocol' : 'SASL_SSL',
        'sasl.mechanism' : 'PLAIN',
        'sasl.username' : "TC63BVUZ2QBK53MJ",
        'sasl.password' : "o6QLpc2EYD9qHyxVDFuytoOVZg7RROkrSwKly7DEQz+nK3HrJrytY1p6btO8gsub",
        'schema.registry.url' : 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
        'schema.registry.basic.auth.credentials.source' : 'USER_INFO',
        'schema.registry.basic.auth.user.info' : 'A3SEGZ3CFXOXB27S:5Z8K+cJ61/JpIjeYf+smXiT21t8nlYCqZ6KaLHUeJnq8cxNHDchBxKH5vFnbQ6cR'
        },default_value_schema = SavedSchema)



topic = 'weather'
try:
    p.produce(topic=topic, value=randomWeather, partition=1)
    print('Message queued:')
    pprint(randomWeather)

except Exception as e:
    pprint(randomWeather)
    print(e)

p.flush()
