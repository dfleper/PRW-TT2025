package es.prw.features.workorders.parts.service;

import es.prw.features.workorders.parts.domain.WorkOrderPartEntity;
import es.prw.features.workorders.parts.dto.WorkOrderPartDto;

import java.util.List;

public interface WorkOrderPartService {

    List<WorkOrderPartEntity> listByWorkOrder(Long workOrderId);

    WorkOrderPartEntity addPart(Long workOrderId, WorkOrderPartDto dto);

    WorkOrderPartEntity updatePart(Long partId, WorkOrderPartDto dto);

    void deletePart(Long partId);
}
