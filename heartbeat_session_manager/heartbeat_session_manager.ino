#include <SPI.h>
#include <Ethernet.h>
#include <AES.h>
#include <sha256.h>

#define NELEMS(x)  (sizeof(x) / sizeof(x[0]))

byte mac[] = { 0x90, 0xA2, 0xDA, 0x0D, 0x77, 0x25 };
char server[] = "192.168.0.13";
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

byte session_id[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };

byte padding [] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };

byte hash [] = {
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
};

byte cipher [4 * N_BLOCK];
byte check [4 * N_BLOCK];

void init_eth_shield(){
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

void clear_plain(){
  int plain_len = NELEMS(plain);
  
  for(int i = 0; i < plain_len; i++){
    plain[i] = 0x00;
  }
}

void clear_session_id(){
  int session_id_len = NELEMS(session_id);
  
  for(int i = 0; i < session_id_len; i++){
    session_id[i] = 0x00;
  }
}

void clear_padding(){
  int len = NELEMS(padding);
  
  for(int i = 0; i < len; i++){
    padding[i] = 0x00;
  }
}

void clear_hash(){
  int len = NELEMS(hash);
  
  for(int i = 0; i < len; i++){
    hash[i] = 0x00;
  }
}

void init_padding(){
	clear_padding();
	
	// TODO: zero padding or random padding?
}

void init_session_id(){
  
  clear_session_id();  
  
  send_session_id_get_request();
  
  // TODO: initialize the session ID array
}

void send_session_id_get_request(){
  Serial.println("connecting...");
  
  // if you get a connection, report back via serial:
  if (client.connect(server, 8080)) {
    Serial.println("connected");
    // Make a HTTP request:
    client.println("GET /sii-heart-monitor/io/arduino/session_id/user/marco HTTP/1.1");
    client.println("Host: 192.168.0.13");
    client.println("Connection: close");
    client.println();
  } else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
  }
}

void init_plain(byte seq_1, byte seq_2, byte seq_3, byte hb_value){
  clear_plain();
  
  plain[0] = seq_1;
  plain[1] = seq_2;
  plain[2] = seq_3;
  
  // Session ID
  for(int i = 3; i < 15; i++){
    plain[i] = session_id[i-3];
  }
  
  plain[15] = hb_value;
  
  // Padding
  for(int i = 16; i < 32; i++){
    plain[i] = padding[i-16];
  }
  
  // Hash
  for(int i = 32; i < 64; i++){
    plain[i] = hash[i-32];
  }
}

// bits = lunghezza key
// blocks = 
void send_hb_value(int bits, int blocks, byte hb_value, byte seq_num_1, byte seq_num_2, byte seq_num_3)
{
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
  
  byte * padding;
  byte * hash;
  
  init_plain(seq_num_1, byte seq_num_2, byte seq_num_3, hb_value, padding, hash);
  
  plain[0] = 0xC0;
  
  succ = aes.encrypt(plain, cipher);
  
  t1 = micros() - t0;

  Serial.print("encrypt ");
  Serial.print((int) succ);
  Serial.print(" took ");
  Serial.print(t1);
  Serial.println("us");
  
  t0 = micros();

  succ = aes.decrypt(cipher, plain);
  
  t1 = micros() - t0;
  
  Serial.print("decrypt ");
  Serial.print((int) succ);
  Serial.print(" took ");
  Serial.print(t1);
  Serial.println("us");

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

void setup ()
{
  Serial.begin(57600);
  
  init_eth_shield();

  init_session_id();
  init_padding();
  
  Serial.println("testing mode");

  prekey(256, 1);
}

void loop () 
{
  // if there are incoming bytes available 
  // from the server, read them and print them:
  if (client.available()) {
    char c = client.read();
    Serial.println("CHAR: ");
    Serial.print(c);
    Serial.print(" BYTE: ");
    byte b = (byte) c;
    
    Serial.print(b);
    
    Serial.println(" CHAR (di nuovo): ");
    
    char c2 = (char) b;
    
    Serial.println(c2);
  }

  // if the server's disconnected, stop the client:
  if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();

    // do nothing forevermore:
    while(true);
  }

}

