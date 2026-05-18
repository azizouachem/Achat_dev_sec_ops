package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.dto.OperateurDTO;
import tn.esprit.rh.achat.services.IOperateurService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "Gestion des opérateurs")
@RequestMapping("/operateur")
public class OperateurController {

	@Autowired
	IOperateurService operateurService;
	
	// http://localhost:8089/SpringMVC/operateur/retrieve-all-operateurs
	@GetMapping("/retrieve-all-operateurs")
	@ResponseBody
	public List<Operateur> getOperateurs() {
		List<Operateur> list = operateurService.retrieveAllOperateurs();
		return list;
	}

	// http://localhost:8089/SpringMVC/operateur/retrieve-operateur/8
	@GetMapping("/retrieve-operateur/{operateur-id}")
	@ResponseBody
	public Operateur retrieveOperateur(@PathVariable("operateur-id") Long operateurId) {
		return operateurService.retrieveOperateur(operateurId);
	}

	@PostMapping("/add-operateur")
	@ResponseBody
	public Operateur addOperateur(@Valid @RequestBody OperateurDTO opDTO) {
		Operateur op = new Operateur();
		op.setIdOperateur(opDTO.getIdOperateur());
		op.setNom(opDTO.getNom());
		op.setPrenom(opDTO.getPrenom());
		op.setPassword(opDTO.getPassword());
		Operateur operateur = operateurService.addOperateur(op);
		return operateur;
	}

	// http://localhost:8089/SpringMVC/operateur/remove-operateur/{operateur-id}
	@DeleteMapping("/remove-operateur/{operateur-id}")
	@ResponseBody
	public void removeOperateur(@PathVariable("operateur-id") Long operateurId) {
		operateurService.deleteOperateur(operateurId);
	}

	@PutMapping("/modify-operateur")
	@ResponseBody
	public Operateur modifyOperateur(@Valid @RequestBody OperateurDTO opDTO) {
		Operateur op = new Operateur();
		op.setIdOperateur(opDTO.getIdOperateur());
		op.setNom(opDTO.getNom());
		op.setPrenom(opDTO.getPrenom());
		op.setPassword(opDTO.getPassword());
		return operateurService.updateOperateur(op);
	}

	
}
