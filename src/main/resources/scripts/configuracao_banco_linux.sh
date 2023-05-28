#!/bin/bash

# Caminho para o arquivo pg_hba.conf
PG_HBA_CONF="/etc/postgresql/15/main/pg_hba.conf"

echo "Caminho do arquivo de configuracao do banco é ${PG_HBA_CONF}"

# Backup do arquivo pg_hba.conf
BACKUP="${PG_HBA_CONF}.bak"

# Método de autenticação a ser configurado
NOVO_METODO="md5"

# Realiza o backup do arquivo pg_hba.conf
sudo cp $PG_HBA_CONF $BACKUP

echo "Realizado o backup do arquivo para ${PG_HBA_CONF}.bak"

# Altera o método de autenticação para md5 no arquivo pg_hba.conf
sudo sed -i -E '/^local|host/s/ (peer|scram-sha-256)/ md5/' $PG_HBA_CONF

echo "alterado o tipo de autenticaoca do banco para ${NOVO_METODO}. Vou reiniciar o servico..."

# Reinicia o serviço do PostgreSQL
sudo service postgresql restart

echo "Servico postgresql reiniciado."
