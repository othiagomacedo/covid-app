# Covid App

Oi! Este aplicativo foi desenvolvido para obter dados da COVID-19 que aconteceu no mundo buscando por cada país.


## O que posso obter com isso?

De forma simples, você pode obter informações do país sobre a COVID-19, como números de confirmados, falecidos e recuperados, se os mesmos estiverem disponíveis. Vai poder checar isso no monitor (ou pela API) e assim obter as informações de forma mais simples. 
Também é possível comparar dados através dos **benchmarks** e vai poder salvá-los, editar e deletar se quiser.

## Que tecnologias foram usadas?

Basicamente foi tudo [Spring Boot 3](https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0) e o com o [JDK na versão 17.0.6](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

Banco de dados foi inteiramente usado [PostgreSQL](https://www.postgresql.org/)

E também foi usado o [Flyway](https://flywaydb.org/) para versionamento das tabelas do banco

## Como usar?
Existem duas formas de usar a aplicação:

1. Usando pelo [Covid Monitor](https://github.com/othiagomacedo/covid-monitor), ou
2. Consumindo a API

Para cada, segue uma forma de usar.

### Pelo Monitor

O Covid Monitor é bem intuitivo, você pode acessar ele [clicando aqui.](https://localhost:9000/monitor-covid) e fazer um clone,
Obs: Antes de acessar o monitor, você precisa deixar ele on-line ok? ;)

### Pela API

Para isso, voce deve usar as requisições logo abaixo.

##
#### PAÍSES

**Obter país pelo nome**

    [GET] /api/pais/nome={nome}


**Obter país pela Sigla**

    [GET] /api/pais/sigla={sigla}


**Obter todos os países disponíveis**

    [GET] /api/pais


##
#### DADOS DE PAÍSES

**Obter informações de um país pela sigla e data do dia**

    [GET] /api/dados/total/{siglaPais}/{data}


**Obter total de um país pela sigla e um período de data**

    [GET] /api/dados/totais/{siglaPais}/{dataInicial}&{dataFinal}


##
#### BENCHMARKS

**Obter ou gerar um benchmark sobre dois países**

    [GET] /api/bench/get/{paisSigla1}&{paisSigla2}/{dataInicial}&{dataFinal}/{nomebench}

**Obter todos os benchmarks já feitos**

    [GET] /api/bench/get/all

**Editar benchmarks**

    [POST] /api/bench/edit/{id}/{paisSigla1}&{paisSigla2}/{dataInicial}&{dataFinal}/{nomebench}

**Deletar benchmark pelo id**

    [DELETE] /api/bench/del/id={id}

**Deletar benchmark pelo nome**

    [DELETE] /api/bench/del/nome={nomeBench}

