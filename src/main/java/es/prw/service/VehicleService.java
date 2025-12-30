package es.prw.service;

import es.prw.dto.VehicleDto;

import java.util.List;

public interface VehicleService {
    List<VehicleDto> listForClient(String userEmail);
    VehicleDto getForEdit(String userEmail, Long idVehicle);
    void create(String userEmail, VehicleDto dto);
    void update(String userEmail, Long idVehicle, VehicleDto dto);
    void delete(String userEmail, Long idVehicle);
}
