#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "coap-engine.h"

#define MAX_FAN_POWER 5
#define MAX_FAN_TEMP 32
#define MIN_FAN_TEMP 18
#include "sys/log.h"
#define LOG_MODULE "RPL BR"
#define LOG_LEVEL LOG_LEVEL_INFO

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

static int actuator_power = 0;
static int actuator_temperature = 20;
static char tmp[100];
/*
 * A handler function named [resource name]_handler must be implemented for each RESOURCE.
 * A buffer for the response payload is provided through the buffer pointer. Simple resources can ignore
 * preferred_size and offset, but must respect the REST_MAX_CHUNK_SIZE limit for the buffer.
 * If a smaller block size is requested for CoAP, the REST framework automatically splits the data.
 */


RESOURCE(fan,
         "title=\"fan\" \"POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
         res_get_handler,  
         res_post_handler, 
         res_put_handler,
         NULL);



static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
 
printf("Chiamata delle get\n");

sprintf(tmp, "{ \"temperature\": %d, \"power\": %d}", actuator_temperature, actuator_power);
printf("%s\n", tmp);
int length = strlen(tmp);
 
  coap_set_header_content_format(response, APPLICATION_JSON); /* text/plain is the default, hence this option could be omitted. */
  coap_set_header_etag(response, (uint8_t *)&length, 1);
  coap_set_payload(response, tmp, length);
}

static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

// la put la usiamo per alza o abbassare, avremo +1 o -1 


 const char* power_char = NULL;
 const char* temp_char = NULL;
 int temp = 0;
 int power = 0;
bool check = false;

 
	 if(coap_get_post_variable(request, "power", &power_char)) {
		
		// se c'è un valore
	  	sscanf(power_char, "%d\n", &power);
	
		if (power==1){
			printf("Richiesta di alzare la potenza\n");
			if(++actuator_power > MAX_FAN_POWER){
				actuator_power=5;
				printf("Potenza al massimo\n");
			}

		}
		if (power==-1){
			printf("Richiesta di ridurre la potenza\n");
			if(--actuator_power < 0){
				actuator_power=0;
				printf("Potenza al minimo\n");
			}
		}
	   }
	if(coap_get_post_variable(request, "temperature", &temp_char)) {

		// se c'è un valore
	  sscanf(temp_char, "%d\n", &temp);
		if (temp==1){
			printf("Richiesta di alzare la temperature\n");
			if(++actuator_temperature > MAX_FAN_TEMP){
				actuator_temperature=32;
			}

		}
		if (temp==-1){
			printf("Richiesta di alzare la temperatura\n");
			if(--actuator_temperature < MIN_FAN_TEMP){
				actuator_temperature = 18;
			}
		}
	   }
  
	
	sprintf(tmp, "{ \"temperature\": %d, \"power\": %d}", actuator_temperature, actuator_power);
	printf("%s\n", tmp);
	int length = strlen(tmp);

  coap_set_header_content_format(response, APPLICATION_JSON); 
 
  coap_set_header_etag(response, (uint8_t *)&length, 1);
 
	
  	coap_set_payload(response, tmp, length);

	if(check){
	  coap_set_status_code(response, CHANGED_2_04);
	}else{
	coap_set_status_code(response, BAD_REQUEST_4_00);
	}

}





static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

// la post la usiamo per settare a un valore specifico 


 const char* power_char = NULL;
 const char* temp_char = NULL;
 int temp = 0;
 int power = 0;

bool check =false;

	  if(coap_get_post_variable(request, "power", &power_char)) {
		check = true;
		// se c'è un valore
	  	sscanf(power_char, "%d\n", &power);
		
		printf("Richiesta di settare la potenza a %d\n", power);
		actuator_power+=power;
			if(actuator_power > MAX_FAN_POWER){
				actuator_power=5;
				printf("Potenza al massimo\n");
			}
			if(actuator_power < 0){
				actuator_power=0;
				printf("Potenza al minimo\n");
			}
	   }
	if(coap_get_post_variable(request, "temperature", &temp_char)) {
		check = true;
		// se c'è un valore
	  	sscanf(temp_char, "%d\n", &temp);
		actuator_power+=power;
			printf("Richiesta di alzare la temperature\n");
			if(actuator_temperature > MAX_FAN_TEMP){
				actuator_temperature=32;
			}

		
			printf("Richiesta di alzare la temperatura\n");
			if(actuator_temperature < MIN_FAN_TEMP){
				actuator_temperature = 18;
			}
		
	   }
  


sprintf(tmp, "{ \"temperature\": %d, \"power\": %d}", actuator_temperature, actuator_power);
printf("%s\n", tmp);
int length = strlen(tmp);

  
  coap_set_header_content_format(response, APPLICATION_JSON);
 
  coap_set_header_etag(response, (uint8_t *)&length, 1);
	coap_set_payload(response, tmp, length);
	if(check){
	  coap_set_status_code(response, CHANGED_2_04);
	}else{
	coap_set_status_code(response, BAD_REQUEST_4_00);
	}
}























