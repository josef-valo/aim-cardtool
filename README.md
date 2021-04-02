# Cardmon-Tool

Testing tool for accepation/integration tests of the process "cardmon".

The cardmon receives information about from card readers and stores these information into database.  

## Cardmon interface

Cardmon receives/sends binary messages via TCP/IP protocol. Messages are sent/received from card readers and contains card id numbers.

Card readers are identified by IP address and cardNo (index). One computer (one IP address) can have installed several card readers. 

## Cardmon-Tool functions

The main aim is to provide human usable test tool via web client. The test tools simulates card readers and sends card information into cardmon.

### REST API

REST interface is created for automation testing, for detail see http://localhost:8080/swagger-ui.html

Example call of the REST API:
  * card reader state -> *curl -X 'GET' 'http://localhost:8080/card/1'* 
  * card reader states -> *curl -X 'GET' 'http://localhost:8080/card'*
  * card in -> *curl -X 'POST' 'http://localhost:8080/card/1/CARD_ID'*
  * card out -> *curl -X 'DELETE' 'http://localhost:8080/card/1'*

## Cardmon-Tool configuration

 * tcp.server.port - port for receiving messages from cardmon
 * tcp.client.port - cardmon port
 * tcp.client.address - cardmon address
 * cardmon.reader.config - configuration file (format described on https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources-implementations-urlresource)
 * cardmon.reader.count - count of the card readers
