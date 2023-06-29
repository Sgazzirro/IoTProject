#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "dev/leds.h"
#if PLATFORM_SUPPORTS_BUTTON_HAL
#include "dev/button-hal.h"
#else
#include "dev/button-sensor.h"
#endif
#include "contiki.h"
#include "coap-engine.h"
/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "RPL BR"
#define LOG_LEVEL LOG_LEVEL_INFO

#define SECTOR 1
#define TYPE "fan"
/* FIXME: This server address is hard-coded for Cooja and link-local for unconnected border router. */
#define SERVER_EP "coap://[fd00::1]"

#include "Actuators.h"
#define TIME_TO_REGISTER 5*CLOCK_SECOND
static struct etimer registration_timer;
static bool registered=false;
static bool disable=false;
static coap_endpoint_t server_ep;
static coap_message_t request[1];
// Define the resource

extern coap_resource_t fan;

/* Declare and auto-start this file's process */
PROCESS(fan_server, "Fan Server");
PROCESS(actuator_registration, "Actuator Registration");
AUTOSTART_PROCESSES(&actuator_registration, &fan_server);

/* Handling function to handle registration response */
void client_chunk_handler(coap_message_t *response)
			{
			const uint8_t *chunk;
	if(response == NULL) {
		LOG_INFO("Request timed out\n");
		
		return;
	}

	int len = coap_get_payload(response, &chunk);

	if(response -> code == DELETED_2_02){
		registered = false;
		printf("Successfully unregistered!\n");
		leds_on(LEDS_RED);
		leds_off(LEDS_GREEN);
	}

	else if(strncmp((char*)chunk, "OKREG", len) == 0){
		registered = true;
		printf("Successfully registered!\n");
		leds_on(LEDS_GREEN);
		leds_off(LEDS_RED);
	}
	else{
		etimer_restart(&registration_timer);
	}

}

static void
disable_actuator(){
        coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
	printf("--Unsubscribing from Application--\n");

        /* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
        coap_init_message(request, COAP_TYPE_CON, COAP_DELETE, 0);
	coap_set_header_uri_path(request, "/register");

}
			



/*---------------------------------------------------------------------------*/
static void
register_actuator(){
	      /* This way the packet can be treated as pointer as usual. */

        coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
	printf("--Registering to Application--\n");

        /* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
        coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
	coap_set_header_uri_path(request, "/register");

      static char reg_info[100];
	
	if(strcmp(TYPE, "fan") == 0 ){
		sprintf(reg_info, "{ \"sector\": %d , \"type\": %s, \"status\": \"0, 20\"}", SECTOR, TYPE);
		actuator_power = 0;
		actuator_temperature = 20;
	}else{
		sprintf(reg_info, "{ \"sector\": %d , \"type\": %s, \"status\": off }", SECTOR, TYPE);
	}

	printf("%s\n", reg_info);

      coap_set_payload(request, (uint8_t *)reg_info, strlen(reg_info));

    
    
    
}



			




			

PROCESS_THREAD(actuator_registration, ev, data)
{
  
  PROCESS_BEGIN();
  etimer_set(&registration_timer, TIME_TO_REGISTER);
while(1){
  PROCESS_YIELD();
  
  if(ev == button_hal_press_event && registered == true){
	disable = true;
	disable_actuator();
	COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);
  }
  if(ev == button_hal_press_event && disable == true){
	disable = false;
	etimer_restart(&registration_timer);
  }
  if(etimer_expired(&registration_timer) && registered == false && disable == false){
	register_actuator();
  	COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);			 

  }
}



  
	      printf("\n--Done--\n");
 

	  PROCESS_END();
}


/*---------------------------------------------------------------------------*/
PROCESS_THREAD(fan_server, ev, data)
{
  PROCESS_BEGIN();
 coap_activate_resource(&fan, "fan");

#if BORDER_ROUTER_CONF_WEBSERVER
  PROCESS_NAME(webserver_nogui_process);
  process_start(&webserver_nogui_process, NULL);
#endif 
/* BORDER_ROUTER_CONF_WEBSERVER */

  LOG_INFO("Contiki-NG Border Router started\n");
	

  PROCESS_END();
}




