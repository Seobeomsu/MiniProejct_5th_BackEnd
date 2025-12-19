#!/bin/bash
echo "=== Spring Boot start ==="

JAR_PATH=$(ls /home/ec2-user/app/*.jar | head -n 1)

nohup java \
  -jar "$JAR_PATH" \
  --spring.profiles.active=prod \
  > /home/ec2-user/app/app.log 2>&1 &

echo "Started Spring Boot with JAR: $JAR_PATH"
