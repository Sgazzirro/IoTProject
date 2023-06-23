#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "coap-engine.h"


#include "sys/log.h"
#define LOG_MODULE "RPL BR"
#define LOG_LEVEL LOG_LEVEL_INFO


static void res_put_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_delete_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);



RESOURCE(nitro,
         "title=\"nitro\" \"POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
         res_get_handler,  
         res_put_post_handler, 
         res_put_post_handler,
         res_delete_handler);

static bool actuator_status = false;
char tmp[100];

static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
 

	if (actuator_status){
	sprintf(tmp, "{ \"status\": on}");
	}else{sprintf(tmp, "{ \"status\": off }");
	}

	printf("%s\n", tmp);
	int length = strlen(tmp);
	 
  coap_set_header_content_format(response, APPLICATION_JSON); 
  coap_set_header_etag(response, (uint8_t *)&length, 1);
  coap_set_payload(response, tmp, length);
}

static void res_put_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

	if(!actuator_status){
	  coap_set_status_code(response, CHANGED_2_04);
	}else{
	coap_set_status_code(response, BAD_REQUEST_4_00);
	}
	// ATTIVA SEMPRE
	actuator_status=true;
	sprintf(tmp, "{ \"status\": on }");
	printf("%s\n", tmp);
	int length = strlen(tmp);

	  coap_set_header_content_format(response, APPLICATION_JSON); 
	 
	  coap_set_header_etag(response, (uint8_t *)&length, 1);
	 
	
  		coap_set_payload(response, tmp, length);

	

}


static void res_delete_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{


// DISATTIVA SEMPRE 


	if(actuator_status){
	  coap_set_status_code(response, CHANGED_2_04);
	}else{
	coap_set_status_code(response, BAD_REQUEST_4_00);
	}

	actuator_status=false;
	sprintf(tmp, "{ \"status\": off }");
	printf("%s\n", tmp);
	int length = strlen(tmp);

	  coap_set_header_content_format(response, APPLICATION_JSON); 
	 
	  coap_set_header_etag(response, (uint8_t *)&length, 1);
	 
	
  	coap_set_payload(response, tmp, length);

	
}


















