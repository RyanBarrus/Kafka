import pymongo
import requests
from random import randrange
from confluent_kafka.avro import AvroProducer
from confluent_kafka.avro import CachedSchemaRegistryClient
from pprint import pprint


# get saved keys
import generalconfig as cfg
mongoDB = cfg.pwd['mongoDB']
confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']
confluentSchemaRegistryKey = cfg.pwd['confluentSchemaRegistryKey']
confluentSchemaRegistrySecret = cfg.pwd['confluentSchemaRegistrySecret']
openWeatherMap = cfg.pwd['openWeatherMap']

# get a random city to work with from MongoDB
client = pymongo.MongoClient(
    f"mongodb+srv://ryanbarrus:{mongoDB}@cluster0-wxksn.azure.mongodb.net/test?retryWrites=true&w=majority",
    ssl=True)
weather = client['Weather']
cities = weather['Cities']
CityCount = cities.count_documents({}) - 1
RandomCity = randrange(0, CityCount)
CityID = cities.find().limit(1).skip(RandomCity)[0]['_id']

# call the weather api to
r = requests.get(f'http://api.openweathermap.org/data/2.5/weather?id={CityID}&APPID={openWeatherMap}')
randomWeather = r.json()

# API returns json with names beginning with numbers. This function renames these fields.
import functions.WeatherAvroSchemaRenamer as renamer
randomWeather = renamer.rename(randomWeather)


# pprint(r.json())

client = CachedSchemaRegistryClient({
    'url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
    'basic.auth.credentials.source': 'USER_INFO',
    'basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}'
})

SavedSchema = client.get_latest_schema('weather-value')[1]

p = AvroProducer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
                  'security.protocol': 'SASL_SSL',
                  'sasl.mechanism': 'PLAIN',
                  'sasl.username': confluentKey,
                  'sasl.password': confluentSecret,
                  'schema.registry.url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
                  'schema.registry.basic.auth.credentials.source': 'USER_INFO',
                  'schema.registry.basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}'
                  }, default_value_schema=SavedSchema)

topic = 'weather'
try:
    p.produce(topic=topic, value=randomWeather, partition=1)
    print('Message queued:')
    pprint(randomWeather)

except Exception as e:
    pprint(randomWeather)
    print(e)

p.flush()
