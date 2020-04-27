from confluent_kafka import Producer

p = Producer({'bootstrap.servers': "13.82.6.66:9092"})

topic = 'Training'

p.produce(topic, value='Lets learn kafka', partition=1)
p.produce(topic, value='testing', partition=1)
p.produce(topic, value='Lets learn kafka', partition=1)
p.produce(topic, value='Lets learn kafka', partition=1)

p.produce(topic, value='testing', partition=1)
a = p.produce(topic, value='Lets learn kafka', partition=1)

# for i in range(10):
#     value = f'Record: {i}'
#     p.produce(topic, value=value, partition=1)


p.pro

p.flush()


print(f"10 messages were produced to topic {topic}!")




