import requests
from datetime import datetime
from pprint import pprint
from confluent_kafka.avro import AvroProducer
from confluent_kafka.avro import CachedSchemaRegistryClient

# get saved keys
import generalconfig as cfg
confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']
confluentSchemaRegistryKey = cfg.pwd['confluentSchemaRegistryKey']
confluentSchemaRegistrySecret = cfg.pwd['confluentSchemaRegistrySecret']

r = requests.get('https://covid-19api.com/api/states-latest?filter[country]=US')
covid = r.json()


client = CachedSchemaRegistryClient({
    'url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
    'basic.auth.credentials.source': 'USER_INFO',
    'basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}'
})

SavedSchema = client.get_latest_schema('covid-value')[1]

p = AvroProducer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
                  'security.protocol': 'SASL_SSL',
                  'sasl.mechanism': 'PLAIN',
                  'sasl.username': confluentKey,
                  'sasl.password': confluentSecret,
                  'schema.registry.url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
                  'schema.registry.basic.auth.credentials.source': 'USER_INFO',
                  'schema.registry.basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}'
                  }, default_value_schema=SavedSchema)

topic = 'covid'

for state in covid:
    if state['country'] == 'US':
        del state['country']
        state['timestamp'] = int(datetime.strptime(state['date'][0:10], '%Y-%m-%d').timestamp())
        del state['date']

        try:
            p.produce(topic=topic, value=state, partition=1)
            print('Message queued:')
            pprint(state)

        except Exception as e:
            pprint(state)
            print(e)

        p.flush()
