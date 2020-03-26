from confluent_kafka import Consumer

# get saved keys
import generalconfig as cfg

confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']

c = Consumer({'bootstrap.servers': "pkc-41973.westus2.azure.confluent.cloud:9092",
        'security.protocol' : 'SASL_SSL',
        'sasl.mechanism' : 'PLAIN',
        'sasl.username' : confluentKey,
        'sasl.password' : confluentSecret,
        'group.id': 'newgroup2',
        })

c.subscribe(['Training'])

while True:
    msg = c.poll(20)

    if msg is None:
        break

    val = msg.value().decode('utf-8')

    print(f'Received message: {val}')

c.close()