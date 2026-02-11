package es.prw.features.availability.service;

import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.appointments.repository.AppointmentRepository;
import es.prw.features.appointments.service.AvailabilityService;
import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

  @Mock AppointmentRepository appointmentRepository;
  @Mock ServiceRepository serviceRepository;

  @InjectMocks AvailabilityService availabilityService;

  @Test
  void disponible_cuandoNoHayCitas() {
    when(appointmentRepository.existsOverlap(any(), any(), eq(AppointmentStatus.CANCELADA)))
        .thenReturn(false);

    boolean ok = availabilityService.isAvailable(
        LocalDateTime.of(2026, 2, 10, 10, 0),
        LocalDateTime.of(2026, 2, 10, 11, 0)
    );

    assertThat(ok).isTrue();
  }

  @Test
  void noDisponible_cuandoHaySolapeParcial() {
    when(appointmentRepository.existsOverlap(any(), any(), eq(AppointmentStatus.CANCELADA)))
        .thenReturn(true);

    boolean ok = availabilityService.isAvailable(
        LocalDateTime.of(2026, 2, 10, 10, 30),
        LocalDateTime.of(2026, 2, 10, 11, 30)
    );

    assertThat(ok).isFalse();
  }

  @Test
  void disponible_cuandoTerminaJustoCuandoEmpiezaOtra_endIgualStart() {
    when(appointmentRepository.existsOverlap(any(), any(), eq(AppointmentStatus.CANCELADA)))
        .thenReturn(false);

    boolean ok = availabilityService.isAvailable(
        LocalDateTime.of(2026, 2, 10, 11, 0),
        LocalDateTime.of(2026, 2, 10, 12, 0)
    );

    assertThat(ok).isTrue();
  }

  @Test
  void ignoraCanceladas_siAplica() {
    when(appointmentRepository.existsOverlap(any(), any(), eq(AppointmentStatus.CANCELADA)))
        .thenReturn(false);

    boolean ok = availabilityService.isAvailable(
        LocalDateTime.of(2026, 2, 10, 10, 30),
        LocalDateTime.of(2026, 2, 10, 10, 45)
    );

    assertThat(ok).isTrue();
    verify(appointmentRepository).existsOverlap(any(), any(), eq(AppointmentStatus.CANCELADA));
  }

  @Test
  void apiCliente_calculaFinConMinutosServicio_yCompruebaSolape() {
    ServiceEntity s = new ServiceEntity();
    s.setActivo(true);
    s.setMinutosEstimados((short) 30);

    when(serviceRepository.findById(10L)).thenReturn(Optional.of(s));
    when(appointmentRepository.existsOverlap(any(), any(), eq(AppointmentStatus.CANCELADA)))
        .thenReturn(false);

    LocalDateTime start = LocalDateTime.of(2026, 2, 10, 10, 0);
    boolean ok = availabilityService.isAvailable(10L, start);

    assertThat(ok).isTrue();
    verify(appointmentRepository).existsOverlap(eq(start), eq(start.plusMinutes(30)), eq(AppointmentStatus.CANCELADA));
  }
}
