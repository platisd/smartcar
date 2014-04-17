/* Arduino code to be used in the smartcar project by Team Pegasus
* as published on https://github.com/platisd/smartcar.git
* A java or android client sends speed and angle data to a raspberry pi
* raspberry pi computes how much speed each motor should have
* and arduino (connected to RPi's USB port) interprets that data
* and drives the motors. An ADAFRUIT motor shield is used based on
* the L293D chip
*/
#include <Servo.h> 
#include <AFMotor.h>
AF_DCMotor motorLeft1(1);
AF_DCMotor motorLeft2(2);
AF_DCMotor motorRight1(3);
AF_DCMotor motorRight2(4);
int incomingByte[2];
boolean tableComplete = false;  // whether the packet is complete
int packetIndex = 0;

void setup() {
  // initialize serial and motors
     Serial.begin(9600);
     motorLeft1.run(RELEASE);
     motorLeft2.run(RELEASE);
     motorRight1.run(RELEASE);
     motorRight2.run(RELEASE);
}

void loop() {
  if (tableComplete) {
      wheelMovement();    
      tableComplete = false; 
  }
}

/* this is called when it's to be decided how the wheel movement
 * should change */
void wheelMovement(){
  
     //left wheels
     //scale it from 8. depends on your input-scale
      int leftSpeed = map(abs(incomingByte[0]),0,8,0,255);       
      if (incomingByte[0]>0){
        //if input is larger than 0, it should go forward
            motorLeft1.run(FORWARD);
            motorLeft2.run(FORWARD);
      }else{
              motorLeft1.run(BACKWARD);
              motorLeft2.run(BACKWARD);
      }
      //"write" the speed to the motor after u have set the direction
      motorLeft1.setSpeed(leftSpeed);
      motorLeft2.setSpeed(leftSpeed);

      //right wheels
      //scale it from 8. depending on your input
      int rightSpeed = map(abs(incomingByte[1]),0,8,0,255);
      if (incomingByte[1]>0){
            motorRight1.run(FORWARD);
            motorRight2.run(FORWARD);        
      }else{
            motorRight1.run(BACKWARD);
            motorRight2.run(BACKWARD);         
      }
      //"write" the speed to the motor after u have set the direction
            motorRight1.setSpeed(rightSpeed);
            motorRight2.setSpeed(rightSpeed);
}
 
 /* serial event occurs between every loop() by default*/
void serialEvent() {
                // read the incoming byte as an integer
                incomingByte[packetIndex] = Serial.read();
                if (incomingByte[packetIndex] > 127 ){
                  //transforming unsigned byte to signed int
                incomingByte[packetIndex] = incomingByte[packetIndex]-256;
                }
                if (packetIndex == 1){
                  tableComplete = true;
                }
                //packet index depends on the length of the byte array you are sending
                packetIndex = (packetIndex + 1) % 2;

  }
