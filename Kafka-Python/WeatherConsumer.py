from confluent_kafka.avro import AvroConsumer
from confluent_kafka.avro.serializer import SerializerError
from pprint import pprint

# get saved keys
import generalconfig as cfg

confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']
confluentSchemaRegistryKey = cfg.pwd['confluentSchemaRegistryKey']
confluentSchemaRegistrySecret = cfg.pwd['confluentSchemaRegistrySecret']

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

for i in range(10):
    try:
        msg = c.poll(timeout=20)

    except SerializerError as e:
        print("Message deserialization failed, skipping bad message.")
        continue

    if msg is None:
        break

    weatherUpdate = msg.value()



    pprint(weatherUpdate)
