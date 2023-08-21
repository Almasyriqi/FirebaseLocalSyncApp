#include <ESP8266WiFi.h>
#define WIFI_SSID "SSID"
#define WIFI_PASSWORD "PASSWORD"

#include <FirebaseESP8266.h>
#define API_KEY "AIzaSyCYd9-Fyu5ZrkO03ylbSaoSxEIQ8OKaPTQ"
#define DATABASE_URL "https://cableandroid-e6c98-default-rtdb.asia-southeast1.firebasedatabase.app"
#define FIREBASE_AUTH "FfOhkBYbqwTaw6OqRqTarHyKraC9ucGdlH56VQmP"

WiFiServer server(12345);
FirebaseData fbdo;
int data = 0, lock = 0, isInternet = 0;

void setup() {
  Serial.begin(115200);
  Serial.println();
  pinMode(BUILTIN_LED, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  server.begin();
  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  Firebase.begin(DATABASE_URL, FIREBASE_AUTH);

  Firebase.reconnectWiFi(true);
  fbdo.setBSSLBufferSize(512, 2048);
}

void loop() {
  // Cek koneksi internet
  if (Firebase.RTDB.getInt(&fbdo, "/Control/data")) {
    isInternet = 1;
  } else {
    isInternet = 0;
  }

  if (isInternet == 1)
  {
    digitalWrite(LED_BUILTIN, LOW);
    if (Firebase.RTDB.getInt(&fbdo, "/Control/data")) {
      //      Serial.println(fbdo.dataType());
      if (fbdo.dataType() == "int") {
        data = fbdo.intData();
        Serial.print("Data dari android : ");
        Serial.println(data);
      }
    } else {
      Serial.println(fbdo.errorReason());
    }

    if (data > 0) {
      Serial.print("Set async... ");
      lock = 1;
      Firebase.setIntAsync(fbdo, "/Control/lock", lock);
      delay(3000);
      lock = 2;
      Firebase.setIntAsync(fbdo, "/Control/lock", lock);
      delay(4000);
      lock = 0;
      data = 0;
      Firebase.setIntAsync(fbdo, "/Control/lock", lock);
      Firebase.setIntAsync(fbdo, "/Control/data", data);
      Serial.println("ok");
    }
  }
  else {
    digitalWrite(LED_BUILTIN, HIGH);
    //    Serial.println("Firebase Tidak Terhubung");
    WiFiClient client = server.available();
    if (client) {
      Serial.println("Ada Client");
      Serial.print("Client connected with IP:");
      Serial.println(client.remoteIP());
      String dt = client.readStringUntil('\n');

      Serial.print("Data dari android : ");
      data = dt.toInt();
      Serial.println(data);
    }
  }
}
