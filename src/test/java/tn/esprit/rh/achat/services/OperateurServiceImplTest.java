package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.repositories.OperateurRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperateurServiceImplTest {

    @Mock
    private OperateurRepository operateurRepository;

    @InjectMocks
    private OperateurServiceImpl operateurService;

    @Test
    void testRetrieveAllOperateurs() {
        List<Operateur> list = new ArrayList<>();
        list.add(new Operateur());
        list.add(new Operateur());

        when(operateurRepository.findAll()).thenReturn(list);

        List<Operateur> result = operateurService.retrieveAllOperateurs();
        assertEquals(2, result.size());
        verify(operateurRepository, times(1)).findAll();
    }

    @Test
    void testAddOperateurWithPassword() {
        Operateur op = new Operateur();
        op.setNom("John");
        op.setPassword("myPassword123");

        when(operateurRepository.save(any(Operateur.class))).thenReturn(op);

        Operateur result = operateurService.addOperateur(op);
        assertNotNull(result);
        assertNotEquals("myPassword123", result.getPassword());
        assertTrue(result.getPassword().contains(":")); // Salted and hashed with PBKDF2
        verify(operateurRepository, times(1)).save(op);
    }

    @Test
    void testAddOperateurWithEmptyPassword() {
        Operateur op = new Operateur();
        op.setNom("John");
        op.setPassword("");

        when(operateurRepository.save(any(Operateur.class))).thenReturn(op);

        Operateur result = operateurService.addOperateur(op);
        assertNotNull(result);
        assertEquals("", result.getPassword());
        verify(operateurRepository, times(1)).save(op);
    }

    @Test
    void testDeleteOperateur() {
        doNothing().when(operateurRepository).deleteById(1L);

        operateurService.deleteOperateur(1L);
        verify(operateurRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateOperateurPlaintextPassword() {
        Operateur op = new Operateur();
        op.setNom("John");
        op.setPassword("newPassword456");

        when(operateurRepository.save(any(Operateur.class))).thenReturn(op);

        Operateur result = operateurService.updateOperateur(op);
        assertNotNull(result);
        assertNotEquals("newPassword456", result.getPassword());
        assertTrue(result.getPassword().contains(":"));
        verify(operateurRepository, times(1)).save(op);
    }

    @Test
    void testUpdateOperateurAlreadyHashedPassword() {
        Operateur op = new Operateur();
        op.setNom("John");
        op.setPassword("salt:alreadyhashedpwd");

        when(operateurRepository.save(any(Operateur.class))).thenReturn(op);

        Operateur result = operateurService.updateOperateur(op);
        assertNotNull(result);
        assertEquals("salt:alreadyhashedpwd", result.getPassword()); // Should not re-hash
        verify(operateurRepository, times(1)).save(op);
    }

    @Test
    void testRetrieveOperateur() {
        Operateur op = new Operateur();
        op.setIdOperateur(1L);
        op.setNom("John");

        when(operateurRepository.findById(1L)).thenReturn(Optional.of(op));

        Operateur result = operateurService.retrieveOperateur(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdOperateur());
        verify(operateurRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveOperateurNotFound() {
        when(operateurRepository.findById(1L)).thenReturn(Optional.empty());

        Operateur result = operateurService.retrieveOperateur(1L);
        assertNull(result);
        verify(operateurRepository, times(1)).findById(1L);
    }
}
