# Introduction

this project was established during Longhash-Hackathon-Tokyo. Initially to solve certain problem for data trading on blockchain. 

Generally, Alice, as seller of the data, has the data encrypted stored somewhere. Bob, as potential buyer of the data want to exam the data alice has without really get the data in plain text.   

3 Roles are defined here:

A: Data Owner/Seller

B: Buyer

C: Trading Platform e.g. an IOT Blockchain System

SSE (Seachable Symmtric Encrption) is used to ensure that the Party C will never know the META-File in plain but can do the search for buyer. This is mandatory for IoT devices due to resourcen problem.

Others as in a typical SSE, we use separated key for keyword searching as the META-Data encryption key. this ensures in certain circumstances (e.g. blockchain) the buyer get with the query key also the data key problem. 

Rekeying is used to generate a unique copy for Party B, who purchased from A for the data. Also due to the lack of resource on endpoint, rekeying should be done by full nodes in blockchain instead of light nodes such like sensors. using proxy rekeying ensure that the party C never knows the data in plain. 

# Installation: 

NTBD


