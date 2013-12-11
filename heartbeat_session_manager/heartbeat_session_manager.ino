#include <SPI.h>
#include <Ethernet.h>
#include <AES.h>
#include <sha256.h>
#include <Base64.h>

#define NELEMS(x)  (sizeof(x) / sizeof(x[0]))

byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x77, 0x25 };
char server[] = "192.168.0.13";
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


String session_id;
String plain_string;

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

  for(int i = 0; i < plain_string.length(); i++){
    plain[i] = (byte) plain_string.charAt(i);
  }
  
  printByteArrayHexValues(plain, TOTAL_BLOCK_LEN_BYTE, "Plain block before padding");
  
  byte pad_element = 0x00;
  int pad_len = TOTAL_BLOCK_LEN_BYTE - plain_string.length();
  
  Serial.print("PAD Len: ");
  Serial.println(pad_len);
  
  for(int i = 0; i < pad_len; i++){
    pad_element++;
  }
  
  Serial.print("PAD Element: ");
  Serial.print(pad_element>>4, HEX);
  Serial.print(pad_element&15, HEX);
  Serial.println();
  
  if(TOTAL_BLOCK_LEN_BYTE - plain_string.length() > 0){
    for(int i = plain_string.length(); i < TOTAL_BLOCK_LEN_BYTE; i++){
      plain[i] = 0x00;
    }
  }
  
  printByteArrayHexValues(plain, TOTAL_BLOCK_LEN_BYTE, "Plain block after padding");
}

void clear_plain(){
  
  plain_string = "";
  
  int plain_len = NELEMS(plain);
  
  for(int i = 0; i < plain_len; i++){
    plain[i] = 0x00;
  }
}

void init_session_id(){
  
  send_session_id_get_request();
  
  session_id = read_http_response_body();
}

void send_session_id_get_request(){
  Serial.println("connecting to send GET (session_id) request...");
  
  // if you get a connection, report back via serial:
  if (client.connect(server, 8080)) {
    Serial.println("connected");
    // Make a HTTP request:
    client.println("GET /sii-heart-monitor/io/arduino/session_id/user/marco HTTP/1.1");
    client.println("Host: 192.168.0.11");
    client.println("Connection: close");
    client.println();
  } else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
  }
}

void send_hb_value_put_request(){
  
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
  
  read_http_response_body();
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

void setup ()
{
  Serial.begin(57600);
  
  init_eth_shield();
  
  init_key();
  init_iv();
  
  print_key();
  print_iv();
  
  clear_plain();
  
  init_session_id();
  
  //init_plain(seq_num_1, seq_num_2, seq_num_3, hb_value);
  
  init_plain();
  
  encrypt_plain();
  
  print_plain_block();
  print_cypher_block();
  print_check_block();
  
  send_hb_value_put_request();
}

void loop () 
{
  // do nothing forevermore:
  while(true);

}



