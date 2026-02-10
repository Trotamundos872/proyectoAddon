

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.proyectoAddons.controller.AddonController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddonController.class)
@AutoConfigureMockMvc(addFilters = false)
class GetInfoTest {

    @Autowired
    private MockMvc mockMvc;

    //TEST DE INTEGRACIÓN

    @Test
    @DisplayName("Debe responder 200 OK")
    void getInfo_ok() throws Exception {
        mockMvc.perform(get("/api/addon"))
               .andExpect(status().isOk());
    }
}
    