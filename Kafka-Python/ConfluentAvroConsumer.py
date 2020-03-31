from confluent_kafka.avro import AvroConsumer
from confluent_kafka.avro.serializer import SerializerError
import pyodbc

# get saved keys
import generalconfig as cfg
confluentKey = cfg.pwd['confluentKey']
confluentSecret = cfg.pwd['confluentSecret']
confluentSchemaRegistryKey = cfg.pwd['confluentSchemaRegistryKey']
confluentSchemaRegistrySecret = cfg.pwd['confluentSchemaRegistrySecret']
sqlServerName = cfg.pwd['sqlServerName']

conn = pyodbc.connect(f'Driver={{SQL Server}};Server={sqlServerName};Database=WORK;Trusted_Connection=yes;')
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

c.subscribe(['Race'])

cursor = conn.cursor()
while True:
    try:
        msg = c.poll(timeout=20)

    except SerializerError as e:
        print("Message deserialization failed, skipping bad message.")
        continue

    if msg is None:
        cursor.commit()
        cursor.close()
        conn.close()
        c.close()
        break

    racer = msg.value()

    cursor.execute(f"INSERT INTO work.dbo.TempRyanKafka VALUES (?,?,?,?,?,?)", [y for x, y in racer.items()])

    print(f'Inserted values into database: {racer}')
