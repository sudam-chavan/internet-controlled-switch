# Internet controlled switch
  This project allows you to control any electric appliance over the internet using ESP8266 NodeMcu and android phone.
  It uses Samsung Artik cloud API to exchange messages over internet.
  
## How to use ?
  - Sign up to Artik cloud console and create device type and add manifest to it. Below is the manifest json from my Artik cloud console.
    You can find step by step guid about manifest [here](https://developer.artik.cloud/documentation/introduction/the-manifest.html)
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
 - Clone project and edit device id and device token fields in arduino sketch as well as android code.
  
## References
 - [Introduction to Artik cloud](https://developer.artik.cloud/documentation/introduction/)
 - [Artik cloud android sdk](https://github.com/artikcloud/artikcloud-java)
