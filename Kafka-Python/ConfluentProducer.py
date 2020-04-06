from confluent_kafka import Producer

# get saved keys
import generalconfig as cfg

confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']

p = Producer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
              'security.protocol': 'SASL_SSL',
              'sasl.mechanism': 'PLAIN',
              'sasl.username': confluentKey,
              'sasl.password': confluentSecret
              })

topic = 'Training'

p.produce(topic, value='Lets learn kafka', partition=1)
p.produce(topic, value='testing', partition=1)
p.produce(topic, value='Lets learn kafka', partition=1)
p.produce(topic, value='Lets learn kafka', partition=1)

p.produce(topic, value='testing', partition=1)
p.produce(topic, value='Lets learn kafka', partition=1)

# for i in range(10):
#     value = f'Record: {i}'
#     p.produce(topic, value=value, partition=1)

p.flush()

print(f"10 messages were produced to topic {topic}!")
