package es.prw.features.availability.api;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.prw.features.appointments.service.AvailabilityService;

@RestController
@RequestMapping("/api")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * GET /api/availability?serviceId=1&startDateTime=2026-01-28T10:00
     * Respuesta: { "available": true }
     */
    @GetMapping("/availability")
    public AvailabilityResponse check(
            @RequestParam(name = "serviceId") Long serviceId,
            @RequestParam(name = "startDateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime
    ) {
        boolean available = availabilityService.isAvailable(serviceId, startDateTime);
        return new AvailabilityResponse(available);
    }
}
