package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.services.IOperateurService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperateurController.class)
@ActiveProfiles("test")
class OperateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IOperateurService operateurService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetOperateurs() throws Exception {
        Operateur op1 = new Operateur();
        op1.setIdOperateur(1L);
        op1.setNom("Nom1");
        
        Operateur op2 = new Operateur();
        op2.setIdOperateur(2L);
        op2.setNom("Nom2");

        List<Operateur> list = Arrays.asList(op1, op2);
        when(operateurService.retrieveAllOperateurs()).thenReturn(list);

        mockMvc.perform(get("/operateur/retrieve-all-operateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Nom1"))
                .andExpect(jsonPath("$[1].nom").value("Nom2"));
    }

    @Test
    void testRetrieveOperateur() throws Exception {
        Operateur op = new Operateur();
        op.setIdOperateur(1L);
        op.setNom("Nom1");

        when(operateurService.retrieveOperateur(1L)).thenReturn(op);

        mockMvc.perform(get("/operateur/retrieve-operateur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nom1"));
    }

    @Test
    void testAddOperateurSuccess() throws Exception {
        Operateur op = new Operateur();
        op.setNom("John");
        op.setPrenom("Doe");
        op.setPassword("securePassword123");

        when(operateurService.addOperateur(any(Operateur.class))).thenReturn(op);

        mockMvc.perform(post("/operateur/add-operateur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(op)))
                .andExpect(status().isOk());
    }

    @Test
    void testAddOperateurValidationFailure() throws Exception {
        Operateur op = new Operateur();
        op.setNom(""); // Blank name violates validation constraint
        op.setPrenom("Doe");
        op.setPassword("12"); // Too short password violates validation constraint

        mockMvc.perform(post("/operateur/add-operateur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(op)))
                .andExpect(status().isBadRequest()); // Captured by GlobalExceptionHandler
    }

    @Test
    void testRemoveOperateur() throws Exception {
        doNothing().when(operateurService).deleteOperateur(1L);

        mockMvc.perform(delete("/operateur/remove-operateur/1"))
                .andExpect(status().isOk());

        verify(operateurService, times(1)).deleteOperateur(1L);
    }

    @Test
    void testModifyOperateur() throws Exception {
        Operateur op = new Operateur();
        op.setNom("John");
        op.setPrenom("Doe");
        op.setPassword("securePassword123");

        when(operateurService.updateOperateur(any(Operateur.class))).thenReturn(op);

        mockMvc.perform(put("/operateur/modify-operateur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(op)))
                .andExpect(status().isOk());
    }
}
