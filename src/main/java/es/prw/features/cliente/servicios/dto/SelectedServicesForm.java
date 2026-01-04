package es.prw.features.cliente.servicios.dto;

import java.util.ArrayList;
import java.util.List;

public class SelectedServicesForm {

    private List<Long> serviceIds = new ArrayList<>();

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }
}
