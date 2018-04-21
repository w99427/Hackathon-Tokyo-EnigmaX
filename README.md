# Hackathon-Tokyo-EnigmaX
Using SSE and Rekeying to demonstrating an typical secure data trade process. 

3 Roles are defined here:
A: Data Owner/Seller
B: Buyer
C: Trading Platform e.g. an IOT Blockchain System

SSE (Seachable Symmtric Encrption) is used to ensure that the Party C will never know the META-File in plain but can do the search for buyer. 
Rekeying is used to generate a unique copy for Party B, who purchased from A for the data. 

Others as in typical SSE, we use separated key for keyword searching as the META-Data encryption key. this ensures in certain circumstances (e.g. blockchain) the buyer get with the query key also the data key problem. 



