package es.prw.app.cliente.servicios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.prw.features.catalog.service.ServiceCatalogService;
import es.prw.features.cliente.servicios.web.ClienteServiciosController;


import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(controllers = ClienteServiciosController.class)
class ClienteServiciosControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean
    ServiceCatalogService catalog;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void list_ok_as_cliente() throws Exception {
        mvc.perform(get("/cliente/servicios"))
           .andExpect(status().isOk())
           .andExpect(view().name("cliente/servicios/list"));
    }
}
