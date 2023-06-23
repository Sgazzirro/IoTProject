#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
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


static bool registered=false;
// Define the resource
extern coap_resource_t fan;
extern coap_resource_t nitro;

/* Declare and auto-start this file's process */
PROCESS(fan_server, "Fan Server");
PROCESS(actuator_registration, "Actuator Registration");
void client_chunk_handler(coap_message_t *response)
			{
			  const uint8_t *chunk;

			  if(response == NULL) {
			    puts("Request timed out");
			    return;
			  }

			 coap_get_payload(response, &chunk);
				
			 if(strcmp((char *)chunk,"OK")==0){
			  	printf("RISPOSTA| %s \n",(char *)chunk);
				registered=true;
				}else{
				printf("there was a error!\n");
				registered=false;
			}


			}
			



/*---------------------------------------------------------------------------*/


			
			AUTOSTART_PROCESSES(&actuator_registration, &fan_server);



			

			PROCESS_THREAD(actuator_registration, ev, data)
			{
			  static coap_endpoint_t server_ep;
			  PROCESS_BEGIN();

while(!registered){

			  static coap_message_t request[1];      /* This way the packet can be treated as pointer as usual. */

			  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);


				     printf("--Registering to Application--\n");

				      /* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
				      coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
				      coap_set_header_uri_path(request, "/register");

				      char reg_info[100];
					
					if(strcmp(TYPE, "fan") == 0 ){
						sprintf(reg_info, "{ \"Sector\": %d , \"Type\": %s, \"Power\": 0, \"Temperature\": 20 }", SECTOR, TYPE);
					}else{
						sprintf(reg_info, "{ \"Sector\": %d , \"Type\": %s, \"Status\": off }", SECTOR, TYPE);
					}

					printf("%s\n", reg_info);

				      coap_set_payload(request, (uint8_t *)reg_info, strlen(reg_info));

				    
				    
				      COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);
}

				      printf("\n--Done--\n");
			 

				  PROCESS_END();
				}


/*---------------------------------------------------------------------------*/
PROCESS_THREAD(fan_server, ev, data)
{
  PROCESS_BEGIN();
 coap_activate_resource(&fan, "fan");
 coap_activate_resource(&nitro, "nitro");

#if BORDER_ROUTER_CONF_WEBSERVER
  PROCESS_NAME(webserver_nogui_process);
  process_start(&webserver_nogui_process, NULL);
#endif 
/* BORDER_ROUTER_CONF_WEBSERVER */

  LOG_INFO("Contiki-NG Border Router started\n");

  PROCESS_END();
}




