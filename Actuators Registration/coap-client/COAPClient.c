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

/* Log configuration */
#include "coap-log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL  LOG_LEVEL_APP

/* FIXME: This server address is hard-coded for Cooja and link-local for unconnected border router. */
#define SERVER_EP "coap://[fd00::1]"

#define TOGGLE_INTERVAL 10

PROCESS(er_example_client, "Erbium Example Client");
AUTOSTART_PROCESSES(&er_example_client);

static struct etimer et;



/* This function is will be passed to COAP_BLOCKING_REQUEST() to handle responses. */
void
client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;

  if(response == NULL) {
    puts("Request timed out");
    return;
  }

  int len = coap_get_payload(response, &chunk);

  printf("RISPOSTA|%.*s", len, (char *)chunk);
}
PROCESS_THREAD(er_example_client, ev, data)
{
  static coap_endpoint_t server_ep;
  PROCESS_BEGIN();

  static coap_message_t request[1];      /* This way the packet can be treated as pointer as usual. */

  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

  etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);

//if(etimer_expired(&et)) {
//	 while(1) {
	   PROCESS_YIELD();

	    if(etimer_expired(&et)) {
	     printf("--Toggle timer--\n");

	      /* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
	      coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
	      coap_set_header_uri_path(request, "/hello");

	      const char msg[] = "Toggle!";

	      coap_set_payload(request, (uint8_t *)msg, sizeof(msg) - 1);

	      LOG_INFO_COAP_EP(&server_ep);
	      LOG_INFO_("\n");

	      COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

	      printf("\n--Done--\n");

//	      etimer_reset(&et);


//	    }
//	}  
}

	  PROCESS_END();
	}
