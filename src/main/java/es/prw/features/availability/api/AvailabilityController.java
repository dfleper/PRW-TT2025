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
     * GET /api/availability?serviceId=1&start=2025-12-28T10:00
     * Respuesta: { "available": true }
     */
    @GetMapping("/availability")
    public AvailabilityResponse check(
            @RequestParam Long serviceId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start
    ) {
        boolean available = availabilityService.isAvailable(serviceId, start);
        return new AvailabilityResponse(available);
    }

    public record AvailabilityResponse(boolean available) {}
}
