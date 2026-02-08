/*
 * package es.prw.config.dev;
 * 
 * import es.prw.features.employees.domain.EmployeeType; import
 * es.prw.features.employees.repository.EmployeeRepository; import
 * org.springframework.boot.CommandLineRunner; import
 * org.springframework.context.annotation.Profile; import
 * org.springframework.stereotype.Component;
 * 
 * @Component
 * 
 * @Profile("dev") public class DevEmployeeCheckRunner implements
 * CommandLineRunner {
 * 
 * private final EmployeeRepository employeeRepository;
 * 
 * public DevEmployeeCheckRunner(EmployeeRepository employeeRepository) {
 * this.employeeRepository = employeeRepository; }
 * 
 * @Override public void run(String... args) { var mecanicos =
 * employeeRepository.findActivosByTipo(EmployeeType.MECANICO);
 * System.out.println("MECANICOS ACTIVOS: " + mecanicos.size());
 * mecanicos.forEach(e -> System.out.println(" - employeeId=" + e.getId() +
 * " userId=" + e.getUser().getIdUser()) ); } }
 */