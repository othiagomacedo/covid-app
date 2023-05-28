# Covid App

Oi! Este aplicativo foi desenvolvido para obter dados da COVID-19 que aconteceu no mundo buscando por cada país.


## O que posso obter com isso?

De forma simples, você pode obter informações do país sobre a COVID-19, como números de confirmados, falecidos e recuperados, se os mesmos estiverem disponíveis. Vai poder checar isso no monitor (ou pela API) e assim obter as informações de forma mais simples. 
Também é possível comparar dados através dos **benchmarks** e vai poder salvá-los, editar e deletar se quiser.


## Como usar?
Existem duas formas de usar a aplicação:

1. Usando pelo monitor, ou
2. Consumindo a API

Para cada, segue uma forma de usar.

### Pelo Monitor

Nosso monitor é bem intuitivo, você pode acessar ele [clicando aqui.](https://localhost:9000/monitor-covid)
Obs: Antes de acessar o monitor, você precisa deixar ele on-line ;)

### Pela API

Para isso, voce deve usar as requisições HTTP logo abaixo.

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

**Obter informações do país pela sigla e data do dia**

    [GET] /api/dados/total/{siglaPais}/{data}


**Obter total do país pela sigla e um período de data**

    [GET] /api/dados/totais/{siglaPais}/{dataInicial}&{dataFinal}


##
#### BENCHMARKS

**Obter benchmark sobre dois países**

    [GET] /api/bench/get/{paisSigla1}&{paisSigla2}/{dataInicial}&{dataFinal}/{nomebench}

**Obter todos os benchmarks**

    [GET] /api/bench/get/all

**Deletar benchmark pelo id dele**

    [DELETE] /api/bench/del/id={id}

**Deletar benchmark pelo nome dele**

    [DELETE] /api/bench/del/nome={nomeBench}

