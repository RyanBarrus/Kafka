from confluent_kafka.avro import AvroProducer
from confluent_kafka.avro import CachedSchemaRegistryClient
import pandas as pd

# get saved keys
import generalconfig as cfg
confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']
confluentSchemaRegistryKey = cfg.pwd['confluentSchemaRegistryKey']
confluentSchemaRegistrySecret = cfg.pwd['confluentSchemaRegistrySecret']

client = CachedSchemaRegistryClient({
    'url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
    'basic.auth.credentials.source': 'USER_INFO',
    'basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}'
})

SavedSchema = client.get_latest_schema('Race-value')[1]

p = AvroProducer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
                  'security.protocol': 'SASL_SSL',
                  'sasl.mechanism': 'PLAIN',
                  'sasl.username': confluentKey,
                  'sasl.password': confluentSecret,
                  'schema.registry.url': 'https://psrc-4r0k9.westus2.azure.confluent.cloud',
                  'schema.registry.basic.auth.credentials.source': 'USER_INFO',
                  'schema.registry.basic.auth.user.info': f'{confluentSchemaRegistryKey}:{confluentSchemaRegistrySecret}'
                  }, default_value_schema=SavedSchema)

topic = 'Race'
dfRace = pd.read_csv('Resources/Race.csv', encoding='utf-8')

for i,row in dfRace.iterrows():
    Race = row.to_dict()
    p.produce(topic=topic, value=Race, partition=1)
    print(f'Message queued: {Race}')

p.flush()