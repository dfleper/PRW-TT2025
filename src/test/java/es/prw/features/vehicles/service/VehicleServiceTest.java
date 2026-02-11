package es.prw.features.vehicles.service;

import es.prw.features.cliente.vehiculos.dto.VehicleDto;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
import es.prw.features.cliente.vehiculos.service.VehicleServiceImpl;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

  @Mock VehicleRepository vehicleRepository;
  @Mock UserRepository userRepository;
  @Mock CustomerRepository customerRepository;

  @InjectMocks VehicleServiceImpl vehicleService;

  @Test
  void update_noPermiteEditarVehiculoDeOtroUsuario_devuelve404() {
    String email = "otro@tt2025.local";

    UserEntity user = new UserEntity();
    user.setIdUser(200L);
    when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(user));

    CustomerEntity customer = new CustomerEntity();
    customer.setIdCustomer(999L);
    customer.setUser(user);
    when(customerRepository.findByUser_IdUser(200L)).thenReturn(Optional.of(customer));

    when(vehicleRepository.findByIdVehicleAndCustomer_IdCustomer(1L, 999L))
        .thenReturn(Optional.empty());

    VehicleDto dto = new VehicleDto();
    dto.setMatricula("9999ZZZ");
    dto.setMarca("Ford");
    dto.setModelo("Focus");

    assertThatThrownBy(() -> vehicleService.update(email, 1L, dto))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(ex -> {
          ResponseStatusException rse = (ResponseStatusException) ex;
          if (rse.getStatusCode() != HttpStatus.NOT_FOUND) {
            throw new AssertionError("Se esperaba 404 NOT_FOUND pero fue " + rse.getStatusCode());
          }
        });

    verify(vehicleRepository).findByIdVehicleAndCustomer_IdCustomer(1L, 999L);
  }

  @Test
  void update_siEmailNoExiste_devuelve401() {
    String email = "noexiste@tt2025.local";
    when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.empty());

    VehicleDto dto = new VehicleDto();
    dto.setMatricula("9999ZZZ");
    dto.setMarca("Ford");
    dto.setModelo("Focus");

    assertThatThrownBy(() -> vehicleService.update(email, 1L, dto))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(ex -> {
          ResponseStatusException rse = (ResponseStatusException) ex;
          if (rse.getStatusCode() != HttpStatus.UNAUTHORIZED) {
            throw new AssertionError("Se esperaba 401 UNAUTHORIZED pero fue " + rse.getStatusCode());
          }
        });
  }
}
