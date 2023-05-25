# Covid App

Oi! Este aplicativo foi desenvolvido para obter dados da COVID-19 que aconteceu no mundo e em cada país.


## O que posso obter com isso?

De forma simples, você pode obter informações do país sobre a COVID-19, como números de confirmados, falecidos e recuperados, se os mesmos estiverem disponíveis. Vai poder checar isso no monitor e assim obter as informações de forma mais simples.


## Como usar?
Existem duas formas de usar a aplicação:

1. Usando pelo monitor, ou
2. Consumindo a API

Para cada, segue uma forma de usar.

### Pelo Monitor

All your files and folders are presented as a tree in the file explorer. You can switch from one to another by clicking a file in the tree.

### Pela API

Para isso, voce deve usar as requisições HTTP logo abaixo. Cada uma delas tem uma breve explicação de como preencher.


**Obter país pelo nome**

    /api/pais/nome={nome}


**Obter país pela Sigla**

    /api/pais/sigla={sigla}


**Obter todos os países disponíveis**

    /api/pais


**Obter informações do país pela sigla e data do dia**

    /api/dados/total/{siglaPais}/{data}


**Obter total do país pela sigla e um período de data**

    /api/dados/totais/{siglaPais}/{dataInicial}&{dataFinal}


**Obter benchmark sobre dois países**

    /api/bench/get/{paisSigla1}&{paisSigla2}/{dataInicial}&{dataFinal}/{nomebench}