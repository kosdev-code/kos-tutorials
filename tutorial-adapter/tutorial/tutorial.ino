#define BAUD 115200

#define LED_PIN 13
#define PIN_BUTTON 14


void setup() {
  Serial.begin(BAUD);
  pinMode(LED_PIN, OUTPUT);
  pinMode(PIN_BUTTON, INPUT);
}

boolean isLow = true;

long waitTime = 0;

void loop() {
  // check for message from java
  if (Serial.available() >= 10) {
    String message = Serial.readString();
    if (message.indexOf("ILLUMINATE") != -1) {
      digitalWrite(LED_PIN, HIGH);
    }
    if (message.indexOf("EXTINGUISH") != -1) {
      digitalWrite(LED_PIN, LOW);
    }
  }

  if (waitTime < millis()) {
    if (isLow && digitalRead(PIN_BUTTON) == HIGH) {
      isLow = false;
      Serial.println("BUTTON");
      waitTime = millis() + 300; // Debouncing
    }
    if (digitalRead(PIN_BUTTON) == LOW) {
      isLow = true;
    }

  }

}
