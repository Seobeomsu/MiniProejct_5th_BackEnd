#!/bin/bash
echo "=== BeforeInstall ==="

mkdir -p /home/ec2-user/app
mkdir -p /home/ec2-user/app/logs

java -version || exit 1
