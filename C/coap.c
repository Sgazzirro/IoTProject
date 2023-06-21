#include "contiki.h"
#include "coap-engine.h"
/* Log configuration */
#include "sys/log.h"
#include "sys/ctimer.h"
#define LOG_MODULE "RPL BR"
#define LOG_LEVEL LOG_LEVEL_INFO
#define SEND_INTERVAL (5 * CLOCK_SECOND)
static struct etimer periodic_timer;
// Define the resource
extern coap_resource_t res_event;
/* Declare and auto-start this file's process */
PROCESS(contiki_ng_br, "Contiki-NG Border Router");
AUTOSTART_PROCESSES(&contiki_ng_br);

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(contiki_ng_br, ev, data)
{
  PROCESS_BEGIN();
  etimer_set(&periodic_timer, SEND_INTERVAL);
  coap_activate_resource(&res_event, "obs");
#if BORDER_ROUTER_CONF_WEBSERVER
  PROCESS_NAME(webserver_nogui_process);
  process_start(&webserver_nogui_process, NULL);
#endif /* BORDER_ROUTER_CONF_WEBSERVER */

  LOG_INFO("Contiki-NG Border Router started\n");

  while(1) {
       	PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&periodic_timer));
        /* Call the event_handler for this application-specific event. */
        res_event.trigger();

        
      }
  PROCESS_END();
}
