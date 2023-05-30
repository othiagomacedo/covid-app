@echo off

set PG_HBA_CONF=C:\Program Files\PostgreSQL\15\data\pg_hba.conf
set BACKUP=%PG_HBA_CONF%.bak
set NOVO_METODO=md5

echo Caminho do arquivo de configuração do banco é %PG_HBA_CONF%

rem Realiza o backup do arquivo pg_hba.conf
copy %PG_HBA_CONF% %BACKUP%

echo Realizado o backup do arquivo para %BACKUP%

rem Altera o método de autenticação para md5 no arquivo pg_hba.conf
powershell -Command "(gc %PG_HBA_CONF%) -replace '(^local|^host).*?(peer|scram-sha-256)$', '$1$2md5' | Out-File -encoding ASCII %PG_HBA_CONF%"

echo Alterado o tipo de autenticação do banco para %NOVO_METODO%. Vou reiniciar o serviço...

rem Reinicia o serviço do PostgreSQL
net stop postgresql-15
net start postgresql-15

echo Serviço PostgreSQL reiniciado.
