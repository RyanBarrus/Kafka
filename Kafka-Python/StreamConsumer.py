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

c.subscribe(['StreamResults'])

while True:
    msg = c.poll(200)

    val = msg.value().decode('utf-8')
    key = msg.key()

    print(f'Key: {key} Value: {val}')

c.close()

