#!/bin/bash
echo "=== Spring Boot stop ==="

PID=$(pgrep -f 'java.*\.jar')

if [ -n "$PID" ]; then
  echo "Stopping process: $PID"
  kill -15 $PID
  sleep 5
else
  echo "No running Spring Boot process"
fi
