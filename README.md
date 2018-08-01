# Internet controlled switch(IOT)
  This project allows you to control any electric appliance over the internet using ESP8266 NodeMcu and an android phone.
  It uses Samsung Artik cloud API to exchange messages over the internet.
  
## How to use ?
  - Sign up to Artik cloud console and create device type. Note down the "device id" and "device token" for the device that you just created. We will need those for arduino and android programs.
  - After device is created, you will also need to add manifest to it. You can find step by step guide about manifest [here](https://developer.artik.cloud/documentation/introduction/the-manifest.html). 
  - Below is a sample manifest from my Artik cloud console.
```json
    {
      "actions": [
        {
          "name": "setOn",
          "description": "makes switch on",
          "parameters": [],
          "isStandard": true,
          "type": "CUSTOM"
       },
       {
          "name": "setOff",
          "description": "makes switch off",
          "parameters": [],
          "isStandard": true,
          "type": "CUSTOM"
      }
    ],
    "fields": [
     {
        "name": "state",
        "type": "CUSTOM",
        "valueClass": "Boolean",
        "isCollection": false,
        "description": "denotes state of switch",
        "tags": []
      }
    ],
    "messageFormat": "json"
  }
```
 - After cloning this project, make sure you have updated device id and device token fields in arduino sketch as well as in an android code. 
 - Finally, connect all components as shown in below image and power it up.

 ![Alt text](images/connections.jpg?raw=true "Connections schematic")
 

Thats it!!!! Once the status LED connected to ESP8266 glows, you can use android app to control the relay(to which you can connect any electric appliance) connected to it from anywhere in the world.
  
## References
 - [Introduction to Artik cloud](https://developer.artik.cloud/documentation/introduction/)
 - [Artik cloud android sdk](https://github.com/artikcloud/artikcloud-java)
