package es.prw.features.appointments.repository;

import es.prw.features.appointments.domain.AppointmentEntity;
import es.prw.features.appointments.domain.AppointmentStatus;
import es.prw.features.catalog.domain.ServiceEntity;
import es.prw.features.catalog.repository.ServiceRepository;
import es.prw.features.cliente.vehiculos.domain.VehicleEntity;
import es.prw.features.cliente.vehiculos.repository.VehicleRepository;
import es.prw.features.iam.domain.CustomerEntity;
import es.prw.features.iam.domain.UserEntity;
import es.prw.features.iam.repository.CustomerRepository;
import es.prw.features.iam.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class AppointmentRepositoryTest {

  @Container
  static final MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:11.4")
      .withDatabaseName("tt2025_test")
      .withUsername("tt")
      .withPassword("tt");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", mariadb::getJdbcUrl);
    r.add("spring.datasource.username", mariadb::getUsername);
    r.add("spring.datasource.password", mariadb::getPassword);

    r.add("spring.flyway.enabled", () -> true);
    r.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    r.add("spring.jpa.open-in-view", () -> false);
  }

  @Autowired AppointmentRepository appointmentRepository;
  @Autowired UserRepository userRepository;
  @Autowired CustomerRepository customerRepository;
  @Autowired VehicleRepository vehicleRepository;
  @Autowired ServiceRepository serviceRepository;

  @Test
  void existsOverlap_devuelveTrue_cuandoHaySolapeParcial() {
    var base = fixture();
    crearCita(base.customer, base.vehicle, base.service,
        LocalDateTime.of(2026, 2, 10, 10, 0),
        LocalDateTime.of(2026, 2, 10, 11, 0),
        AppointmentStatus.CONFIRMADA
    );

    boolean solapa = appointmentRepository.existsOverlap(
        LocalDateTime.of(2026, 2, 10, 10, 30),
        LocalDateTime.of(2026, 2, 10, 11, 30),
        AppointmentStatus.CANCELADA
    );

    assertThat(solapa).isTrue();
  }

  @Test
  void existsOverlap_devuelveFalse_cuandoEndEsIgualAStart() {
    var base = fixture();
    crearCita(base.customer, base.vehicle, base.service,
        LocalDateTime.of(2026, 2, 10, 10, 0),
        LocalDateTime.of(2026, 2, 10, 11, 0),
        AppointmentStatus.CONFIRMADA
    );

    boolean solapa = appointmentRepository.existsOverlap(
        LocalDateTime.of(2026, 2, 10, 11, 0),
        LocalDateTime.of(2026, 2, 10, 12, 0),
        AppointmentStatus.CANCELADA
    );

    assertThat(solapa).isFalse();
  }

  @Test
  void existsOverlap_ignoraCanceladas_siSePasaCanceladaComoExclusion() {
    var base = fixture();
    crearCita(base.customer, base.vehicle, base.service,
        LocalDateTime.of(2026, 2, 10, 10, 0),
        LocalDateTime.of(2026, 2, 10, 11, 0),
        AppointmentStatus.CANCELADA
    );

    boolean solapa = appointmentRepository.existsOverlap(
        LocalDateTime.of(2026, 2, 10, 10, 30),
        LocalDateTime.of(2026, 2, 10, 11, 0),
        AppointmentStatus.CANCELADA
    );

    assertThat(solapa).isFalse();
  }

  // ===== helpers =====

  private record Fixture(CustomerEntity customer, VehicleEntity vehicle, ServiceEntity service) {}

  private Fixture fixture() {
    UserEntity u = new UserEntity();
    u.setEmail("fixture@tt2025.local");
    u.setPasswordHash("{noop}test");
    u.setNombre("Test");
    u.setApellidos("Fixture");
    u.setActivo(true);
    u = userRepository.saveAndFlush(u);

    CustomerEntity c = new CustomerEntity();
    c.setUser(u);
    c = customerRepository.saveAndFlush(c);

    VehicleEntity v = new VehicleEntity();
    v.setCustomer(c);
    v.setMatricula("1234ABC");
    v.setMarca("Marca");
    v.setModelo("Modelo");
    v.setActivo(true);
    v = vehicleRepository.saveAndFlush(v);

    ServiceEntity s = new ServiceEntity();
    s.setCodigo("SVC-TST");
    s.setNombre("Servicio Test");
    s.setDescripcion("Test");
    s.setPrecioBase(new BigDecimal("10.00"));
    s.setMinutosEstimados((short) 60);
    s.setActivo(true);

    // Tu ServiceEntity no tiene @PrePersist: timestamps por seguridad
    LocalDateTime now = LocalDateTime.now();
    s.setCreatedAt(now);
    s.setUpdatedAt(now);

    s = serviceRepository.saveAndFlush(s);

    return new Fixture(c, v, s);
  }

  private void crearCita(CustomerEntity c, VehicleEntity v, ServiceEntity s,
                         LocalDateTime inicio, LocalDateTime fin, AppointmentStatus estado) {

    AppointmentEntity a = new AppointmentEntity();
    a.setCustomer(c);
    a.setVehicle(v);
    a.setService(s);
    a.setInicio(inicio);
    a.setFin(fin);
    a.setEstado(estado);

    appointmentRepository.saveAndFlush(a);
  }
}
