#include <SPI.h>
#include <Ethernet.h>
#include <AES.h>
#include <sha256.h>
#include <Base64.h>

#define NELEMS(x)  (sizeof(x) / sizeof(x[0]))

byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x77, 0x25 };
char server[] = "192.168.0.15";
String server_string = "192.168.0.15";
EthernetClient client;

AES aes;
String key_string = "SECRET_KEY_OF_32SECRET_KEY_OF_32"; // 32 chars
byte key[] = 
{
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
}; // 32B = 256b
String iv_string = "MY_IV_VECTOR_CBC";
byte iv[] = 
{
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
} ;

String user_name;
String session_id;
String plain_string;
String hb_put_request_response;
int hb_put_request_response_code;

const int BLOCK_LEN_BYTE = 16;
const int BLOCKS = 4;
const int TOTAL_BLOCK_LEN_BYTE = BLOCKS * BLOCK_LEN_BYTE;

byte plain [TOTAL_BLOCK_LEN_BYTE];
byte cipher [TOTAL_BLOCK_LEN_BYTE];
byte check [TOTAL_BLOCK_LEN_BYTE];

const int KEY_ARRAY_LEN_BYTE = 32;
const int KEY_ARRAY_LEN_BIT = KEY_ARRAY_LEN_BYTE * 8;
const int IV_ARRAY_LEN_BYTE = 16;

char URL_FRIENDLY_CHAR = '-';

// OK and ERROR CODES
const int ERROR_CODE_NOT_RECOGNIZED_INT = -1;

const char OK_CODE = '0';
const int OK_CODE_INT = 0;

const char USER_NOT_AVAILABLE_ERROR = '1';
const int USER_NOT_AVAILABLE_ERROR_INT = 1;

const char SESSION_NOT_INITIALIZED = '2';
const int SESSION_NOT_INITIALIZED_INT = 2;

const char INVALID_SEQ_NUMBER = '4';
const char INVALID_SEQ_NUMBER_INT = 4;

int seq_number;

// Variables for Pulse sensor

int pulsePin = 0;                 // Pulse Sensor purple wire connected to analog pin 0
int blinkPin = 13;                // pin to blink led at each beat

// these variables are volatile because they are used during the interrupt service routine!
volatile int BPM;                   // used to hold the pulse rate
volatile int Signal;                // holds the incoming raw data
volatile int IBI = 600;             // holds the time between beats, must be seeded! 
volatile boolean Pulse = false;     // true when pulse wave is high, false when it's low
volatile boolean QS = false;        // becomes true when Arduoino finds a beat.

void print_plain_block(){
  
  Serial.print("Plain block: ");
  
  for(int i = 0; i < NELEMS(plain); i++){
    byte val = plain[i];
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void print_cypher_block(){
  
  Serial.print("Cypher block: ");
  
  for(int i = 0; i < NELEMS(cipher); i++){
    byte val = cipher[i];
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void print_check_block(){
  
  Serial.print("Check block: ");
  
  for(int i = 0; i < NELEMS(check); i++){
    byte val = check[i];
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void print_key(){
  
  Serial.print("Key: ");
  Serial.println(key_string);
  
  Serial.print("Key (byte array): ");
  
  for(int i = 0; i < NELEMS(key); i++){
    byte val = key[i];
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void print_iv(){
  
  Serial.print("IV: ");
  Serial.println(iv_string);
  
  Serial.print("IV (byte array): ");
  
  for(int i = 0; i < NELEMS(iv); i++){
    byte val = iv[i];
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void printByteArrayHexValues(byte * arr, unsigned int len, String array_name){
  
  Serial.print(array_name + " (byte array): ");
  
  for(int i = 0; i < len; i++){
    byte val = arr[i];
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void printCharArrayHexValues(char * arr, unsigned int len, String array_name){
  
  Serial.print(array_name + " (byte array): ");
  
  for(int i = 0; i < len; i++){
    char c = arr[i];
    byte val = (byte) c;
    Serial.print(val>>4, HEX);
    Serial.print(val&15, HEX);
    Serial.print(" ");
  }
  
  Serial.println();
}

void init_key(){
  for(int i = 0; i < key_string.length(); i++){
    key[i] = (byte) key_string.charAt(i);
  }
}

void init_iv(){
  for(int i = 0; i < iv_string.length(); i++){
    iv[i] = (byte) iv_string.charAt(i);
  }
}

void init_plain(){
  clear_plain();
  
  plain_string = plain_string + session_id;
  
  int content_len = plain_string.length();
  
  // init Session ID
  for(int i = 0; i < content_len; i++){
    plain[i] = (byte) plain_string.charAt(i);
  }
  
  // init HB values
  plain[content_len] = highByte(BPM);
  content_len++;
  plain[content_len] = lowByte(BPM);
  content_len++;
  plain[content_len] = highByte(IBI);
  content_len++;
  plain[content_len] = lowByte(IBI);
  content_len++;
  
  // init sequence_number
  plain[content_len] = highByte(seq_number);
  content_len++;
  plain[content_len] = lowByte(seq_number);
  content_len++;
  
  printByteArrayHexValues(plain, TOTAL_BLOCK_LEN_BYTE, "Plain block before padding");
  
  byte pad_element = 0x00;
  int pad_len = TOTAL_BLOCK_LEN_BYTE - content_len;
  
  Serial.print("PAD Len: ");
  Serial.println(pad_len);
  
  for(int i = 0; i < pad_len; i++){
    pad_element++;
  }
  
  Serial.print("PAD Element: ");
  Serial.print(pad_element>>4, HEX);
  Serial.print(pad_element&15, HEX);
  Serial.println();
  
  if(TOTAL_BLOCK_LEN_BYTE - content_len > 0){
    for(int i = content_len; i < TOTAL_BLOCK_LEN_BYTE; i++){
      plain[i] = 0x00;
    }
  }
  
  printByteArrayHexValues(plain, TOTAL_BLOCK_LEN_BYTE, "Plain block after padding");
}

void increment_seq_number(){
  seq_number++;
}

void print_plain_elements(){
  Serial.println("Session ID: " + session_id);
  
  Serial.print("BPM: ");
  Serial.println(BPM);
  
  Serial.print("IBI: ");
  Serial.println(IBI);
  
  Serial.print("SEQ_N: ");
  Serial.println(seq_number);
}

void clear_plain(){
  
  plain_string = "";
  
  int plain_len = NELEMS(plain);
  
  for(int i = 0; i < plain_len; i++){
    plain[i] = 0x00;
  }
}

void wait(unsigned long delayTime){
  Serial.print("Waiting ");
  Serial.print(delayTime);
  Serial.println(" ms");
  delay(delayTime);
}

void send_user_name_request_read_answer(unsigned long delayTime){
  
  if(delayTime > 0){
    wait(delayTime);
  }
  
  Serial.println("Retrying now");
  
  send_user_name_get_request();
  user_name = read_http_response_body();
}

void send_session_id_request_read_answer(unsigned long delayTime){
  
  if(delayTime > 0){
    wait(delayTime);
  }
  
  Serial.println("Retrying now");
  
  send_session_id_get_request();
  session_id = read_http_response_body();
}

int evaluate_error_code(char received_error_code, boolean do_default_recovery_action){
  
 Serial.print("Received an error code: ");
 Serial.println(received_error_code);
 
 int ret = ERROR_CODE_NOT_RECOGNIZED_INT;
  
 if(received_error_code == USER_NOT_AVAILABLE_ERROR){ // There is no logged user OR the user has not yet started a session
   Serial.println("Error: did not receive a username (user not available or user did not yet start a session)");
   
   // Wait and retry
   if(do_default_recovery_action){
     send_user_name_request_read_answer(10000);   
   }
   
   ret = USER_NOT_AVAILABLE_ERROR_INT;
 }else if(received_error_code == SESSION_NOT_INITIALIZED) {
   Serial.println("Error: did not receive a session_id (user did not yet start a session)");
   
   // Wait and retry
   if(do_default_recovery_action){
     send_session_id_request_read_answer(10000);
   }
   
   ret = SESSION_NOT_INITIALIZED_INT;
 }else if(received_error_code == OK_CODE){
   ret = OK_CODE_INT;
 }else if(received_error_code == INVALID_SEQ_NUMBER){   
   ret = INVALID_SEQ_NUMBER_INT;
 }else{
   Serial.println("Error code not recognized.");   
   ret = ERROR_CODE_NOT_RECOGNIZED_INT;
 }
 
 return ret;
}

void init_user_name(){
  
  user_name = "";
  send_user_name_request_read_answer(0);
  
  int error_code_evaluation_result = ERROR_CODE_NOT_RECOGNIZED_INT;
  
  while(user_name.length() <= 1){
   if(user_name.length() == 1){ // it's an error code
     Serial.println("Received an error code while requesting username");
     error_code_evaluation_result = evaluate_error_code(user_name.charAt(0), true);
     
     if(error_code_evaluation_result == ERROR_CODE_NOT_RECOGNIZED_INT){
       Serial.println("Error code not recognized. Retrying anyway.");
       
       send_user_name_request_read_answer(5000);
     }
     
   }else{ // server did not answer
     Serial.println("Received no answer while requesting username");

     send_user_name_request_read_answer(5000);
   }
 }
}

void init_session_id(){
  
  session_id = "";
  
  send_session_id_request_read_answer(0);
  
  int error_code_evaluation_result = ERROR_CODE_NOT_RECOGNIZED_INT;
  
  while(session_id.length() <= 1){
   if(session_id.length() == 1){ // it's an error code
     Serial.println("Received an error code while requesting session_id");
     error_code_evaluation_result = evaluate_error_code(session_id.charAt(0), true);
     
     if(error_code_evaluation_result == ERROR_CODE_NOT_RECOGNIZED_INT){
       Serial.println("Error code not recognized. Retrying anyway.");
       
       send_session_id_request_read_answer(5000);
     }
     
   }else{ // server did not answer
     Serial.println("Received no answer while requesting session_id");

     send_session_id_request_read_answer(5000);
   }
 }
}

void send_user_name_get_request(){
  Serial.println("connecting to send GET (user_name) request...");
  
  // if you get a connection, report back via serial:
  if (client.connect(server, 8080)) {
    Serial.println("connected");
    // Make a HTTP request:
    client.println("GET /sii-heart-monitor/io/arduino/current_user HTTP/1.1");
    client.println("Host: " + server_string);
    client.println("Connection: close");
    client.println();
  } else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
  }
}

void send_session_id_get_request(){
  Serial.println("connecting to send GET (session_id) request...");
  
  // if you get a connection, report back via serial:
  if (client.connect(server, 8080)) {
    Serial.println("connected");
    // Make a HTTP request:
    client.println("GET /sii-heart-monitor/io/arduino/session_id/user/" + user_name + " HTTP/1.1");
    client.println("Host: " + server_string);
    client.println("Connection: close");
    client.println();
  } else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
  }
}

void send_hb_value_put_request(){
  hb_put_request_response = "";
  hb_put_request_response_code = 0;
  
  int argument_len = TOTAL_BLOCK_LEN_BYTE;
  
  char put_request_argument[TOTAL_BLOCK_LEN_BYTE];
  
  for(int i = 0; i < TOTAL_BLOCK_LEN_BYTE; i++){
    put_request_argument[i] = (char) cipher[i];
  }
  
  printCharArrayHexValues(put_request_argument, argument_len, "PUT request argument");
  
  int encoded_put_request_argument_length = base64_enc_len(argument_len);
  char encoded_put_request_argument_array[encoded_put_request_argument_length];
  base64_encode(encoded_put_request_argument_array, put_request_argument, argument_len);
  
  String encoded_put_request_argument_dirty = String(encoded_put_request_argument_array);
  
  Serial.println("Encoded PUT request argument (dirty): " + encoded_put_request_argument_dirty);
  printCharArrayHexValues(encoded_put_request_argument_array, encoded_put_request_argument_length, "Encoded PUT request argument (dirty)");
  
  // cleanup for URL
  for(int i = 0; i < encoded_put_request_argument_length; i++){
    if(encoded_put_request_argument_array[i] == '/'){
      encoded_put_request_argument_array[i] = URL_FRIENDLY_CHAR;
    }
  }
  
  String encoded_put_request_argument = String(encoded_put_request_argument_array);
  
  Serial.print("Encoded PUT request argument (replaced / with ");
  Serial.print(URL_FRIENDLY_CHAR);
  Serial.print(" to be URL friendly): ");
  Serial.println(encoded_put_request_argument);
  
  printCharArrayHexValues(encoded_put_request_argument_array, encoded_put_request_argument_length, "Encoded PUT request argument");
  
  Serial.println("connecting to send PUT request");
  
  // if you get a connection, report back via serial:
  if (client.connect(server, 8080)) {
    Serial.println("connected");
    
    String http_put_request = "PUT /sii-heart-monitor/io/arduino/session/store/";
    http_put_request = http_put_request + encoded_put_request_argument + " HTTP/1.1";
    
    Serial.print("HTTP PUT request: ");
    Serial.println(http_put_request);
    
    // Make a HTTP request
    client.println(http_put_request);
    client.println("Host: 192.168.0.11");
    client.println("Connection: close");
    client.println();
  } else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
  }
  
  hb_put_request_response = read_http_response_body();
}

// bits = lunghezza key
// blocks = 
void encrypt_plain()
{
  int bits = KEY_ARRAY_LEN_BIT;
  int blocks = BLOCKS;
  
  long t0 = micros();
  byte succ = aes.set_key(key, bits);
  long t1 = micros() - t0;
  
  Serial.print("set_key ");
  Serial.print(bits);
  Serial.print(" ->");
  Serial.print((int) succ);
  Serial.print(" took ");
  Serial.print(t1);
  Serial.println("us");
  
  t0 = micros();
  
  if(blocks > 1){
    succ = aes.cbc_encrypt(plain, cipher, blocks, iv);
  }else{
    succ = aes.encrypt(plain, cipher);
  }
  
  t1 = micros() - t0;

  Serial.print("encrypt ");
  Serial.print((int) succ);
  Serial.print(" took ");
  Serial.print(t1);
  Serial.println("us");
  
  t0 = micros();
  
  init_iv();
  
  if(blocks > 1){
    succ = aes.cbc_decrypt(cipher, check, blocks, iv);
  }else{
    succ = aes.decrypt(cipher, check);
  }
  
  t1 = micros () - t0 ;
  Serial.print ("decrypt ") ; Serial.print ((int) succ) ;
  Serial.print (" took ") ; Serial.print (t1) ; Serial.println ("us") ;
}

void decrypt_cipher(){

  int bits = KEY_ARRAY_LEN_BIT;
  int blocks = BLOCKS;
  
  long t0 = micros();
  byte succ = aes.set_key(key, bits);
  long t1 = micros() - t0;
  
  Serial.print("set_key ");
  Serial.print(bits);
  Serial.print(" ->");
  Serial.print((int) succ);
  Serial.print(" took ");
  Serial.print(t1);
  Serial.println("us");
  
  t0 = micros();
  
  if(blocks > 1){
    succ = aes.cbc_decrypt(cipher, check, blocks, iv);
  }else{
    succ = aes.decrypt(cipher, check);
  }
  
  t1 = micros () - t0 ;
  
  Serial.print ("decrypt ") ;
  Serial.print ((int) succ) ;
  Serial.print (" took ") ;
  Serial.print (t1) ;
  Serial.println ("us") ;
}

String read_http_response_body(){
  
  while(client.connected() && !client.available()) delay(1);
  
  String response = "";
  
  char c;
  
  while (client.available()) {
        c = client.read();
        response = response + c;
  }
  
  stop_eth_client();
  
  Serial.println("Response: ");
  
  Serial.println(response);
  
  int firstCr = 0;
  int secondCr = 0;
  
  int response_len = response.length();
  
  while(secondCr != firstCr + 2){
    firstCr = response.indexOf('\r', firstCr);
    secondCr = response.indexOf('\r', firstCr + 1);
    
    if(secondCr != firstCr + 2){
      firstCr = secondCr;
    }
 }
  
  int responseBodyStart = secondCr + 2;
  String response_body = response.substring(responseBodyStart);
  
  Serial.print("Response body: ");
  Serial.println(response_body);
    
  return response_body;
}

void stop_eth_client(){
  // if the server's disconnected, stop the client:
  if (!client.connected()) {
    Serial.println("disconnecting.");
    client.stop();
  }
}

void init_eth_shield(){

  Serial.println("ETH Shield init start");
  
  // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    while(true);
  }
  
  Serial.println("ETH Shield init OK");
  
  // give the Ethernet shield a second to initialize:
  delay(1000);
}

void initialize_everything(){
  init_user_name();
  init_session_id();
  
  hb_put_request_response = "";
  hb_put_request_response_code = 0;
  seq_number = 0;
}

void setup ()
{
  Serial.begin(115200);
  
  // PulseSensor init
  pinMode(blinkPin,OUTPUT);         // pin that will blink to your heartbeat!
  interruptSetup();                 // sets up to read Pulse Sensor signal every 2mS 
  
  init_eth_shield();
  
  init_key();
  init_iv();
  
  print_key();
  print_iv();
  
  initialize_everything();
}

void loop () 
{
  if (QS == true){ // Quantified Self flag is true when arduino finds a heartbeat

    //reset the Quantified Self flag for next time
    QS = false;
    
    clear_plain();
    init_plain();
    init_iv();    
    encrypt_plain();
    
    print_plain_elements();
    //print_plain_block();
    //print_cypher_block();
    //print_check_block();
    
    send_hb_value_put_request();
    
    if(hb_put_request_response.length() == 1){
      hb_put_request_response_code = evaluate_error_code(hb_put_request_response.charAt(0), false);
      
      if(hb_put_request_response_code == OK_CODE_INT){
        Serial.println("Value correctly stored");
        increment_seq_number();
      }else if(hb_put_request_response_code == INVALID_SEQ_NUMBER_INT){
        Serial.println("Wrong sequence number. Restart from 0 and try to resync with server");
        seq_number = 0;
        
        while(hb_put_request_response_code == INVALID_SEQ_NUMBER_INT){
          send_hb_value_put_request();
          hb_put_request_response_code = evaluate_error_code(hb_put_request_response.charAt(0), false);
          increment_seq_number();
        }
      }else{
        Serial.print("Something went wrong. Error code: ");
        Serial.println(hb_put_request_response_code);
        initialize_everything();
      }
    }        
  }
  
  delay(20);
  
  if(seq_number > 100){
    // do nothing forevermore:
    while(true); 
  }
}



