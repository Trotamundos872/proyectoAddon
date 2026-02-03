package com.example.demo.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.proyectoAddons.controller.AddonController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebMvcTest(AddonController.class)
@AutoConfigureMockMvc(addFilters = false)

class GetInfoTest {
    @Autowired
    private MockMvc mockMvc;
    //GetInfoTest1();
    
    @Test
    void GetInfoTest1() throws Exception {
        mockMvc.perform(get("/api/addon"))
               .andExpect(status().isOk());      
    }
}
