package es.prw.config.dev;

import es.prw.features.workorders.service.WorkOrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class WorkOrderDebugRunner implements CommandLineRunner {

    private final WorkOrderService workOrderService;

    public WorkOrderDebugRunner(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @Override
    public void run(String... args) {
    	// SELECT id_appointment FROM appointments ORDER BY id_appointment DESC LIMIT 10; 
    	// y el resultado lo ponemos aquí... 
        Long appointmentId = 22L; // <-- CAMBIA ESTO por un id_appointment real que exista
        
        try {
            var wo1 = workOrderService.getOrCreateForAppointment(appointmentId);
            System.out.println("[WO TEST] WO1 id = " + wo1.getId());

            var wo2 = workOrderService.getOrCreateForAppointment(appointmentId);
            System.out.println("[WO TEST] WO2 id = " + wo2.getId());

            System.out.println("[WO TEST] Same? " + wo1.getId().equals(wo2.getId()));
        } catch (Exception ex) {
            System.out.println("[WO TEST] FAILED: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }
}
