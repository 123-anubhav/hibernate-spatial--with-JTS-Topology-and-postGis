🚀 Step-by-Step: Run Spring Boot + Kafka + MQTT + Docker

Docker Compose setup for **Mosquitto (MQTT)**, **Kafka**, and **Zookeeper**, you're ready to run your Spring Boot app and test the full MQTT → Kafka pipeline. Here's a step-by-step guide to get everything working smoothly:

---

## 🚀 Step-by-Step: Run Spring Boot + Kafka + MQTT + Docker

### ✅ 1. Start Your Docker Services

In your project root (where `docker-compose.yml` lives):

```bash
docker-compose up -d
```

This will:
- Start Mosquitto on port `1883`
- Start Kafka on port `9092`
- Start Zookeeper on port `2181`

You don’t need to install Kafka or Zookeeper separately—they’re already containerized.

---

### ✅ 2. Create Kafka Topic (Optional but Recommended)

Kafka can auto-create topics, but it's better to define them explicitly:

```bash
docker exec -it kafka bash
kafka-topics.sh --create --topic vts.location --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

You can also list topics:

```bash
kafka-topics.sh --list --bootstrap-server localhost:9092
```

---

### ✅ 3. Run Your Spring Boot App

Just run it like normal:

```bash
./mvnw spring-boot:run
```

Or from your IDE. Make sure your `application.yml` or `application.properties` points to:

```yaml
spring.kafka.bootstrap-servers=localhost:9092
mqtt.broker=tcp://localhost:1883
```

---

### ✅ 4. Test the Flow

#### 🧪 Publish MQTT Message

From terminal:

```bash
mosquitto_pub -h localhost -t "vehicle/123/location" -m '{"lat":25.4,"lon":81.8}' 
```

Your Spring Boot app should:
- Receive this via MQTT inbound adapter
- Forward it to Kafka topic `vts.location`

#### 🧪 Consume Kafka Message

Add a Kafka listener in your app:

```java
@KafkaListener(topics = "vts.location", groupId = "vts-group")
public void consume(String payload) {
    System.out.println("Received from Kafka: " + payload);
}
```

You’ll see the message printed in your logs.

---

## 🧠 Summary

| Component     | Role                          |
|---------------|-------------------------------|
| Mosquitto     | MQTT broker for device input  |
| Kafka         | Event streaming backbone      |
| Zookeeper     | Kafka coordination            |
| Spring Boot   | MQTT subscriber + Kafka producer |
| Kafka Listener| Kafka consumer for processing |

---

real VTS topics like `vehicle/kanpur/location`, `cms/display/kanpur`, and `alert/geofence`. Let’s build it out.

if issue in kafka software running then fix 

🛠️ Fixing Kafka + Zookeeper Startup Error

[issue maybe comes with kafka images distribution] 
after docker-compose up 
check with 
$ docker ps
is kafka,zookeepr and mqtt runs or not


# docker-compose.yml

version: '3.8'

services:
  mosquitto:
    image: eclipse-mosquitto
    ports:
      - "1883:1883"
    volumes:
      - ./mosquitto/config:/mosquitto/config

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

# 🔄 Now Try Again
Run this to rebuild and relaunch:
docker-compose down -v
docker-compose up --build

This should resolve the type errors and get Kafka + Zookeeper up and running.

Once it's live, you can:
- Create Kafka topics manually or let Spring Boot auto-create them
- Publish MQTT messages to Mosquitto
- Watch them flow into Kafka via your Spring Boot bridg

## testing the kafka + mqtt + boot

Nice work getting Mosquitto pub/sub running, Anubhav! You're right at the edge of full integration. Here's how to proceed step-by-step:

---

## ✅ Step 1: Check Kafka & Zookeeper Status

To verify if Kafka and Zookeeper are running:

### 🔍 Option A: Use Docker CLI

```bash
docker ps
```

Look for containers named something like:
- `anubhav_kafka_1`
- `anubhav_zookeeper_1`

If they’re listed and `STATUS` says “Up”, they’re running.

### 🔍 Option B: Inspect Logs

```bash
docker logs anubhav_kafka_1
docker logs anubhav_zookeeper_1
```

You should see startup logs like:
- Kafka listening on port 9092
- Zookeeper binding to port 2181

If either exited, run:

```bash
docker-compose up -d
```

---

## ✅ Step 2: Create Kafka Topic (Recommended)

Kafka can auto-create topics, but it’s cleaner to define them manually.

### 🛠️ Create Topic

```bash
docker exec -it anubhav_kafka_1 bash
kafka-topics.sh --create \
  --topic vts.location \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

### 📋 List Topics

```bash
kafka-topics.sh --list --bootstrap-server localhost:9092
```

---

## ✅ Step 3: Run Your Spring Boot App

Once Kafka is confirmed running and your topic is created, launch your app:

```bash
./mvnw spring-boot:run
```

Or from your IDE. Make sure your `application.yml` points to:

```yaml
spring.kafka.bootstrap-servers=localhost:9092
mqtt.broker=tcp://localhost:1883
```

---

## ✅ Step 4: Test the Full Flow

### 🧪 Publish MQTT Message

```bash
mosquitto_pub -h localhost -t vehicle/kanpur/location -m '{"vehicleId":"UP65AB1234","lat":25.4,"lon":81.8}'
```

### 📥 Kafka Consumer Output

Your Spring Boot app should log:

```
Received from Kafka: {"vehicleId":"UP65AB1234","lat":25.4,"lon":81.8}
```

---


## in fails issue in kafka some troubleshoot i am sharing

 the Kafka CLI tools aren’t located at `/opt/kafka/bin` inside your container. Confluent’s Kafka image uses a different structure. Let’s get you sorted.

---

## ✅ How to Run Kafka CLI in Confluent Kafka Container

Try this instead:

```bash
docker exec -it anubhav_kafka_1 bash
```

Once inside the container, run:

```bash
kafka-topics --create \
  --topic vts.location \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

> 🔍 Notice: The CLI tool is just `kafka-topics`, not `kafka-topics.sh`, and it’s available in the default `$PATH` in newer Confluent images.

---

## 🧪 Verify Topic Creation

To list topics:

```bash
kafka-topics --list --bootstrap-server localhost:9092
```

To describe your topic:

```bash
kafka-topics --describe --topic vts.location --bootstrap-server localhost:9092
```

---

## 🛠 If That Still Fails…

Run this to locate the actual Kafka CLI path:

```bash
find / -name kafka-topics 2>/dev/null
```

Once you find the correct path (e.g., `/usr/bin/kafka-topics` or `/bin/kafka-topics`), use that full path to run your commands.

---