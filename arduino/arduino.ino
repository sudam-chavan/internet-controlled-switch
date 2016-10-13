#include <MQTTClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

#define SWITCH 15 // Pin D8 on ESP8266. This is connected to relay
#define STATUS_LED 12 // Pin D6 on ESP8266. Indicates MQTT connection status
#define DEBUG

// Artik cloud declarations
const char* mqttCloudServer       = "api.artik.cloud"; // Samsung artik cloud
int  mqttCloudPort                = 8883; //default port
const char* mqttCloudClientName   = "internet-switch"; // or whatever you prefer
const char* mqttCloudUsername     = "<device-id>"; // put your device id
const char* mqttCloudPassword     = "<device-token>"; // put your device token
const char* mqttPublishPath       = "/v1.1/messages/<device-id>"; // Put your device id
const char* mqttSubscribePath     = "/v1.1/actions/<device-id>"; // Put your device id

// field and actions from device manifest
const char* FIELD_STATE  = "state";
const char* ACTION_ON    = "setOn";
const char* ACTION_OFF   = "setOff";

const char* ssid = "<SSID>";      //  your wifi SSID (name)
const char* pass = "<wifi password>"; //  your wifi password
int status = WL_IDLE_STATUS;

WiFiClientSecure ipCloudStack;
MQTTClient mqttCloudClient;
char jsonBuffer[100];
bool isSwitchOn;
  
void setup() {
  #ifdef DEBUG
    Serial.begin(115200);
  #endif
  delay(10);
  
  // set relay pin to output mode
  pinMode(SWITCH, OUTPUT);
  digitalWrite(SWITCH, LOW);

  // set status led pin to output mode
  pinMode(STATUS_LED, OUTPUT);
  digitalWrite(STATUS_LED, LOW);

  // connect to WiFi
  connectToWifi();
  // connect to MQTT server
  mqttConnect();
}
  
void loop() {
  if(WiFi.status() != WL_CONNECTED) {
    #ifdef DEBUG
      Serial.println("wifi is disconnected");
    #endif
    cleanUp();
    connectToWifi();
    mqttConnect();
  } else {
    // Sometimes, MQTT connection breaks. So, check connection status
    if(mqttCloudClient.connected())
      mqttCloudClient.loop(); // Mandatory method call.
    else {
      cleanUp();
      mqttConnect();
    }
  }

  // if connected to MQTT server, glow the status led
  if(mqttCloudClient.connected())
    digitalWrite(STATUS_LED, HIGH);
  else
    digitalWrite(STATUS_LED, LOW);
  delay(1000);
}

// callback for subscribed channel
// example payload json for action: [{"name":"setOn","parameters":{}}]
void messageReceived(String topic, String payload, char * bytes, unsigned int length) {
  #ifdef DEBUG
    Serial.println("message received");
    Serial.println(payload);
  #endif
  if(payload.indexOf(ACTION_ON) > 0) {
    setSwitchOn();
  }else if(payload.indexOf(ACTION_OFF) > 0) {
    setSwitchOff();
  } else {
    #ifdef DEBUG
      Serial.println("no action found");
    #endif
  }
}

void setSwitchOn() {
  digitalWrite(SWITCH, HIGH);
  isSwitchOn = true;
  publishDeviceStatus();
}

void setSwitchOff() {
  digitalWrite(SWITCH, LOW);
  isSwitchOn = false;
  publishDeviceStatus();
}

void connectToWifi() {
  #ifdef DEBUG
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);
  #endif
 
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    #ifdef DEBUG
      Serial.print(".");
    #endif
  }
  #ifdef DEBUG
    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
  #endif
}

void mqttConnect() {
  #ifdef DEBUG
    Serial.println("Connecting to MQTT server");
  #endif
  mqttCloudClient.begin(mqttCloudServer, mqttCloudPort, ipCloudStack);
  mqttCloudClient.connect(mqttCloudClientName, mqttCloudUsername, mqttCloudPassword);
  #ifdef DEBUG
    Serial.println("Connected to MQTT server");
    Serial.println("Subscribing for actions");
  #endif
  
  // After successfull connection, subscribe for actions over MQTT channel
  if(mqttCloudClient.subscribe(mqttSubscribePath)) {
    #ifdef DEBUG
      Serial.println("subscribed for actions");
    #endif
  } else {
    #ifdef DEBUG
        Serial.println("subscription failed");
    #endif
  }
}

void publishDeviceStatus() {
  if(mqttCloudClient.publish(mqttPublishPath, getStatusJson())) {
    #ifdef DEBUG
      Serial.println("status published");
    #endif
  } else {
    #ifdef DEBUG
      Serial.println("error while publishing status");
    #endif
  }
}

void cleanUp() {
  // unsubscribe from actions
  mqttCloudClient.unsubscribe(mqttSubscribePath); 
  // disconnect from MQTT server
  if(mqttCloudClient.disconnect()) {
    #ifdef DEBUG
      Serial.println("Disconnected from MQTT server");
    #endif
  } else {
    #ifdef DEBUG
      Serial.println("Failed to disconnect from MQTT server"); 
    #endif
  }
}

String getStatusJson() {
  // here 1 is number of elements in JSON
  // right now we are generating Json like {"state":false}
  // hence number of element is 1.
  const int BUFFER_SIZE = JSON_OBJECT_SIZE(1);
  StaticJsonBuffer<BUFFER_SIZE> jsonObjectBuffer;
  JsonObject& root = jsonObjectBuffer.createObject();
  root[FIELD_STATE] = isSwitchOn;
  root.printTo(jsonBuffer, sizeof(jsonBuffer));
  return String(jsonBuffer);
}
