package es.prw.features.cliente.vehiculos.service;

import java.util.List;

import es.prw.features.cliente.vehiculos.dto.VehicleDto;

public interface VehicleService {
    List<VehicleDto> listForClient(String userEmail);
    VehicleDto getForEdit(String userEmail, Long idVehicle);
    void create(String userEmail, VehicleDto dto);
    void update(String userEmail, Long idVehicle, VehicleDto dto);
    void delete(String userEmail, Long idVehicle);
}
