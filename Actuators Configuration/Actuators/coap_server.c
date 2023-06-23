
#include "contiki.h"
#include "coap-engine.h"
/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "RPL BR"
#define LOG_LEVEL LOG_LEVEL_INFO
// Define the resource
extern coap_resource_t fan;
extern coap_resource_t nitro;

/* Declare and auto-start this file's process */
PROCESS(fan_server, "Fan Server");
AUTOSTART_PROCESSES(&fan_server);

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

