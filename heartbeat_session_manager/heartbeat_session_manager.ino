#include <SPI.h>
#include <Ethernet.h>
#include <AES.h>
#include <sha256.h>

#define NELEMS(x)  (sizeof(x) / sizeof(x[0]))

byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x77, 0x25 };
char server[] = "192.168.0.11";
EthernetClient client;

AES aes;

byte key[] = 
{
  0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
};

byte plain[] =
{
  0x00, // 0 = sequence number part 1 (up to 256)
  0x00, // 1 = sequence number part 2 (up to 256)
  0x00, // 2 = sequence number part 3 (up to 256)
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 3 to 14 = Session ID (it's a 12-byte MongoDB ObjectID)
  0x00, // 15 = current HB value
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 16 to 31 = padding
  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 32 to 63 = SHA-256 hash of the previous fields
};

byte cipher [4 * N_BLOCK];
byte check [4 * N_BLOCK];

int BLOCK_LEN_BYTE = 64;


void print_plain_block(){
  for(int i = 0; i < BLOCK_LEN_BYTE; i++){
    char c = (char) plain[i];
    
    Serial.print(c);
  }
  
  Serial.println();
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

void clear_plain(boolean clear_session_id){
  int plain_len = NELEMS(plain);
  
  for(int i = 0; i < plain_len; i++){
    if(clear_session_id){
      plain[i] = 0x00;
    }else{
      if(i < 3 || i > 13){
        plain[i] = 0x00;
      }
    }
  }
}

void init_session_id(){
  
  send_session_id_get_request();
  
  String session_id = read_http_response_body();
  
  int session_id_len = session_id.length();
  
  Serial.print("Session ID Len: ");
  Serial.println(session_id_len);
  
  for(int i = 0; i < session_id_len; i++){
    plain[i + 3] = (byte) session_id.charAt(i);
  }
  
  Serial.print("Plain block after session init: ");
  print_plain_block();
}

void init_plain(byte seq_1, byte seq_2, byte seq_3, byte hb_value){
  clear_plain(false);
  
  plain[0] = seq_1;
  plain[1] = seq_2;
  plain[2] = seq_3;
  
  plain[15] = hb_value;
  
  // Padding
  for(int i = 16; i < 32; i++){
    plain[i] = 0x00;
  }
  
  // Hash
  for(int i = 32; i < 64; i++){
    plain[i] = 0x00;
  }
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

// for testing we use the plain block
String cypher_block_to_string(){
  String s = "";
  
  Serial.println("Start block processing");

  for(int i = 0; i < BLOCK_LEN_BYTE; i++){
    
    char c = '0';
    byte b = plain[i];
    if(b != 0x00){
      c = (char) plain[i];
    }

    s = s + String(c);
  }
  
  Serial.println("Done block processing");
  
  return s;
}

void send_hb_value_put_request(){
  
  Serial.println("connecting to send PUT request...");
  
  String put_request_argument = cypher_block_to_string();
  
  Serial.print("PUT request argument: ");
  Serial.println(put_request_argument);
  
    // if you get a connection, report back via serial:
  if (client.connect(server, 8080)) {
    Serial.println("connected");
    
    String http_put_request = "PUT /sii-heart-monitor/io/arduino/session/store/";
    http_put_request = http_put_request + put_request_argument + " HTTP/1.1";
    
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
void send_hb_value(int bits, int blocks, byte hb_value, byte seq_num_1, byte seq_num_2, byte seq_num_3)
{
  //long t0 = micros();
  //byte succ = aes.set_key(key, bits);
  //long t1 = micros() - t0;
  
//  Serial.print("set_key ");
//  Serial.print(bits);
//  Serial.print(" ->");
//  Serial.print((int) succ);
//  Serial.print(" took ");
//  Serial.print(t1);
//  Serial.println("us");
//  
//  t0 = micros();
  
  init_plain(seq_num_1, seq_num_2, seq_num_3, hb_value);
  
//  succ = aes.encrypt(plain, cipher);
  
//  t1 = micros() - t0;

//  Serial.print("encrypt ");
//  Serial.print((int) succ);
//  Serial.print(" took ");
//  Serial.print(t1);
//  Serial.println("us");
//  
//  t0 = micros();
//
//  succ = aes.decrypt(cipher, plain);
//  
//  t1 = micros() - t0;
//  
//  Serial.print("decrypt ");
//  Serial.print((int) succ);
//  Serial.print(" took ");
//  Serial.print(t1);
//  Serial.println("us");

  // ph == 0 stampa PLAIN (prima riga output)
  // ph == 1 stampa CYPHER (seconda riga output)
  // ph == 2 stampa CHECK (CYPHER decriptato) (terza riga output)

  
  for (byte ph = 0; ph < 3; ph++)
  {
    for (byte i = 0; i < (ph < 3 ? blocks * N_BLOCK : N_BLOCK); i++)
    {
      byte val = ph == 0 ? plain[i] : (ph == 1 ? cipher[i] : check[i]);
      Serial.print(val>>4, HEX);
      Serial.print(val&15, HEX);
      Serial.print(" ");
    }
    
    Serial.println();
  }
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
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
  }
}

void setup ()
{
  Serial.begin(57600);
  
  init_eth_shield();
  
  clear_plain(true);
  
  init_session_id();
  
  send_hb_value_put_request();
}

void loop () 
{
  // do nothing forevermore:
  while(true);

}

