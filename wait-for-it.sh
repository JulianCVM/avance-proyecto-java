#!/bin/bash
# Espera a que un host y puerto estén disponibles

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

until nc -z "$host" "$port"; do
  >&2 echo "MySQL en $host:$port no está disponible - esperando..."
  sleep 1
done

>&2 echo "MySQL está disponible - ejecutando comando"
exec $cmd 