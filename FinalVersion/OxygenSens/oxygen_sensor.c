#include "contiki.h"
#include "net/routing/routing.h"
#include "mqtt.h"
#include "net/ipv6/uip.h"
#include "net/ipv6/uip-icmp6.h"
#include "net/ipv6/sicslowpan.h"
#include "sys/etimer.h"
#include "sys/ctimer.h"
#include "lib/sensors.h"
#include "dev/button-hal.h"
#include "dev/leds.h"
#include "os/sys/log.h"
#include "mqtt-client.h"

#include <string.h>
#include <strings.h>
#include <stdlib.h>
#include <sys/node-id.h>
/*---------------------------------------------------------------------------*/
#define LOG_MODULE "mqtt-client"
#ifdef MQTT_CLIENT_CONF_LOG_LEVEL
#define LOG_LEVEL MQTT_CLIENT_CONF_LOG_LEVEL
#else
#define LOG_LEVEL LOG_LEVEL_DBG
#endif

/*---------------------------------------------------------------------------*/
/* MQTT broker address. */
#define MQTT_CLIENT_BROKER_IP_ADDR "fd00::1"

static const char *broker_ip = MQTT_CLIENT_BROKER_IP_ADDR;

// Defaukt config values
#define DEFAULT_BROKER_PORT         1883
#define DEFAULT_PUBLISH_INTERVAL    (30 * CLOCK_SECOND)


// We assume that the broker does not require authentication


/*---------------------------------------------------------------------------*/
/* Various states */
static uint8_t state;

#define STATE_INIT    		  0
#define STATE_NET_OK    	  1
#define STATE_CONNECTING      2
#define STATE_CONNECTED       3
#define STATE_SUBSCRIBED      4
#define STATE_DISCONNECTED    5

/*---------------------------------------------------------------------------*/
PROCESS_NAME(mqtt_client_process);
AUTOSTART_PROCESSES(&mqtt_client_process);

/*---------------------------------------------------------------------------*/
/* Maximum TCP segment size for outgoing segments of our socket */
#define MAX_TCP_SEGMENT_SIZE    32
#define CONFIG_IP_ADDR_STR_LEN   64
/*---------------------------------------------------------------------------*/
/*
 * Buffers for Client ID and Topics.
 * Make sure they are large enough to hold the entire respective string
 */
#define BUFFER_SIZE 64

static char client_id[BUFFER_SIZE];
static char pub_topic[BUFFER_SIZE];
//static char sub_topic[BUFFER_SIZE];

// Value of dust
static int decimal_part = 0;
static int floating_part = 0;
static int button_press = 0;

// Periodic timer to check the state of the MQTT client
#define STATE_MACHINE_PERIODIC     (CLOCK_SECOND >> 1)
static struct etimer periodic_timer;

// Timer di recovery
static struct etimer recovery_timer;

/*---------------------------------------------------------------------------*/
/*
 * The main MQTT buffers.
 * We will need to increase if we start publishing more data.
 */
#define APP_BUFFER_SIZE 512
static char app_buffer[APP_BUFFER_SIZE];
/*---------------------------------------------------------------------------*/
static struct mqtt_message *msg_ptr = 0;

static struct mqtt_connection conn;

mqtt_status_t status;
char broker_address[CONFIG_IP_ADDR_STR_LEN];


/*---------------------------------------------------------------------------*/
PROCESS(mqtt_client_process, "MQTT Client");


// Define necessarie alla simulazione
#define SENSOR_TYPE "oxygen"

static int min_sense = 15;
// Il settore di questo sensore
#define sector 1

static unsigned long time;
static int sent = 0;

// Utility function that changes the minimum value the sensor measure. For simplicity, this is used
// to "simulate" that the actuator has modified the environment and It is called via button press.
// In realistic situations, the value "min_sense" is not necessary since the actuator will truly change 
// the environment and the button will be used for other purposes.
static void
change_min_sense(){
	int choice = button_press % 3;
	if(choice == 0)
		min_sense = 9; 
	if(choice == 1)
		min_sense = 15;
	if(choice == 2)
		min_sense = 4;
	button_press++;

}
static void
sense_oxygen(){
    sprintf(pub_topic, "%s", SENSOR_TYPE);
    decimal_part = (rand() % 5) + min_sense;
    floating_part = rand() % 99;

    time = clock_seconds();

    //set_ip();
    printf("Current value of %s: %d.%d", SENSOR_TYPE, decimal_part, floating_part);


    printf(" at second %lu\n", time);

	sprintf(app_buffer, "{\"IDSensor\" : %d ,  \"sector\" : %d, \"value\" : \"%d.%d\" , \"topic\" : \"%s\" , \"timestamp\" : \"%lu\"}", node_id, sector, decimal_part, floating_part, SENSOR_TYPE, time);
}

static void
publish(){
	sense_oxygen();
	sent = (sent + 1)%5;
	if(sent == 0){
		mqtt_publish(&conn, NULL, pub_topic, (uint8_t *)app_buffer,
       		strlen(app_buffer), MQTT_QOS_LEVEL_0, MQTT_RETAIN_OFF);
	}
}

/*---------------------------------------------------------------------------*/
static void
pub_handler(const char *topic, uint16_t topic_len, const uint8_t *chunk,
            uint16_t chunk_len)
{
  printf("Pub Handler: topic='%s' (len=%u), chunk_len=%u\n", topic,
          topic_len, chunk_len);

  if(strcmp(topic, "actuator") == 0) {
    printf("Received Actuator command\n");
	printf("%s\n", chunk);
    // Do something :)
    return;
  }
}
/*---------------------------------------------------------------------------*/
static void
mqtt_event(struct mqtt_connection *m, mqtt_event_t event, void *data)
{
  switch(event) {
  case MQTT_EVENT_CONNECTED: {
    printf("Application has a MQTT connection\n");
    leds_off(LEDS_RED);
    leds_on(LEDS_GREEN);
    state = STATE_CONNECTED;
    break;
  }
  case MQTT_EVENT_DISCONNECTED: {
    printf("MQTT Disconnect. Reason %u\n", *((mqtt_event_t *)data));
    leds_off(LEDS_GREEN);
    leds_on(LEDS_RED);
    state = STATE_DISCONNECTED;
    process_poll(&mqtt_client_process);
    break;
  }
  case MQTT_EVENT_PUBLISH: {
    msg_ptr = data;

    pub_handler(msg_ptr->topic, strlen(msg_ptr->topic),
                msg_ptr->payload_chunk, msg_ptr->payload_length);
    break;
  }
  case MQTT_EVENT_SUBACK: {
#if MQTT_311
    mqtt_suback_event_t *suback_event = (mqtt_suback_event_t *)data;

    if(suback_event->success) {
      printf("Application is subscribed to topic successfully\n");
    } else {
      printf("Application failed to subscribe to topic (ret code %x)\n", suback_event->return_code);
    }
#else
    printf("Application is subscribed to topic successfully\n");
#endif
    break;
  }
  case MQTT_EVENT_UNSUBACK: {
    printf("Application is unsubscribed to topic successfully\n");
    break;
  }
  case MQTT_EVENT_PUBACK: {
    printf("Publishing complete.\n");
    break;
  }
  default:
    printf("Application got a unhandled MQTT event: %i\n", event);
    break;
  }
}

static bool
have_connectivity(void)
{
  if(uip_ds6_get_global(ADDR_PREFERRED) == NULL ||
     uip_ds6_defrt_choose() == NULL) {
	  printf("No connectivity!");
    return false;
  }
  return true;
}
static struct etimer accensione;

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(mqtt_client_process, ev, data)
{

  PROCESS_BEGIN();

  printf("MQTT Client Process\n");
  printf("Attendo un po'\n");
  etimer_set(&accensione, 60*CLOCK_SECOND);
 while(1){
  PROCESS_YIELD();
  if(etimer_expired(&accensione))
	break;
  }

  // Initialize the ClientID as MAC address
  snprintf(client_id, BUFFER_SIZE, "%02x%02x%02x%02x%02x%02x",
                     linkaddr_node_addr.u8[0], linkaddr_node_addr.u8[1],
                     linkaddr_node_addr.u8[2], linkaddr_node_addr.u8[5],
                     linkaddr_node_addr.u8[6], linkaddr_node_addr.u8[7]);

  // Broker registration
  mqtt_register(&conn, &mqtt_client_process, client_id, mqtt_event,
                  MAX_TCP_SEGMENT_SIZE);

  printf("Sono qui\n");
  state=STATE_INIT;

  // Initialize periodic timer to check the status
  etimer_set(&periodic_timer, 10*CLOCK_SECOND);

  /* Main loop */
  while(1) {
    
    /* Monitored Events :
	- Button pressing -> The minimum value of oxygen sensed has to change
	- PROCESS_EVENT_POLL -> Early awakening because of some errors (MQTT Disconnections)
	- PROCESS_EVENT_TIMER && data == &periodic_timer -> Time to check the state of the sensor (sense a new data or go on MQTT phases)
    */
    PROCESS_YIELD();
    if((ev == PROCESS_EVENT_TIMER && data == &periodic_timer) ||
	      ev == PROCESS_EVENT_POLL || ev == button_hal_press_event){

		   if(ev == button_hal_press_event){
			change_min_sense();
			continue;
		   }

		  /* If enough time has passed from a MQTT disconnection, a recovery procedure retry the entire procedure from the beginning */

		   if(etimer_expired(&recovery_timer) && state == STATE_DISCONNECTED){
			   state = STATE_INIT;
		   }

		  if(state==STATE_INIT){
			 if(have_connectivity()==true)
				 state = STATE_NET_OK;
		  }

		  if(state == STATE_NET_OK){
			  // Connect to MQTT server
			  printf("Connecting!\n");

			  memcpy(broker_address, broker_ip, strlen(broker_ip));

			  mqtt_connect(&conn, broker_address, DEFAULT_BROKER_PORT,
						   (DEFAULT_PUBLISH_INTERVAL * 3) / CLOCK_SECOND,
						   MQTT_CLEAN_SESSION_ON);
			  state = STATE_CONNECTING;
		  }

		  /* FUTURE IMPLEMENTATIONS : Make sensor subscribe to a topic to dinamically change thresholds */

		  /*
		  if(state==STATE_CONNECTED){

			  // Subscribe to a topic
			  strcpy(sub_topic,"actuator");

			  status = mqtt_subscribe(&conn, NULL, sub_topic, MQTT_QOS_LEVEL_0);

			  printf("Subscribing!\n");
			  if(status == MQTT_STATUS_OUT_QUEUE_FULL) {
				LOG_ERR("Tried to subscribe but command queue was full!\n");
				PROCESS_EXIT();
			  }

			  state = STATE_SUBSCRIBED;
		  }*/


		if(state == STATE_CONNECTED){
			publish();

		} else if ( state == STATE_DISCONNECTED ){
		   LOG_ERR("Disconnected form MQTT broker. Trying to reconnect...\n");
		   etimer_set(&recovery_timer, 5*CLOCK_SECOND);

		}

		etimer_restart(&periodic_timer);

    }

  }

  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
