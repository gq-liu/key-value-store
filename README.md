# key-value-store
# Set up
1. Install JDK, recommand JDK 8
2. Install ant, recommand ant 1.9.14
**NOTE**: Ant version and JDK version should compatible.   
This project use ant: Apache Ant(TM) version 1.9.14 and JDK 8   
You can find the ant old release [here](https://ant.apache.org/bindownload.cgi).
And install instruction [here](https://ant.apache.org/manual/install.html). 

# How to run. 
1. Compile the code. 
```shell 
./build.sh
``` 
This will automatically clean and recompile your code.   

2. run the server & client
Go to the directory where has run-server.sh and run-client.sh files, then run following commands.  
```shell 
./run-server.sh
```  
This will run a server and listen at port 8080. To see your local server running, go to "localhost:8080" in your browser.  

```shell 
./run-client.sh host:port /basedir blockSize
```
This will run your client.  
For example:  
```shell
./run-client.sh localhost:8080 ./ 1024  
```
it will return following stuff (with start code) 
```shell
Ping() successful
PutBlock() successful
GetBlock() successfully read in 16 bytes
``` 
And the server side will have following log print out:
```shell
Attempting to start XML-RPC Server...
Started successfully.
Accepting requests. (Halt program to stop.)
Ping()
PutBlock()
GetBlock(h0)
```
