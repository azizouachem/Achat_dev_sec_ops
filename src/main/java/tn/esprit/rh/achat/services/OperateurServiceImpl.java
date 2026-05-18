package tn.esprit.rh.achat.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rh.achat.entities.Operateur;
import tn.esprit.rh.achat.repositories.OperateurRepository;
import tn.esprit.rh.achat.util.PasswordHashUtil;

import java.util.List;

@Service
public class OperateurServiceImpl implements IOperateurService {

	private static final Logger logger = LoggerFactory.getLogger(OperateurServiceImpl.class);

	@Autowired
	OperateurRepository operateurRepository;

	@Override
	public List<Operateur> retrieveAllOperateurs() {
		return (List<Operateur>) operateurRepository.findAll();
	}

	/**
	 * OWASP A02:2021 – Cryptographic Failures
	 * Hash the password before saving to prevent plaintext storage.
	 */
	@Override
	public Operateur addOperateur(Operateur o) {
		if (o.getPassword() != null && !o.getPassword().isEmpty()) {
			o.setPassword(PasswordHashUtil.hashPassword(o.getPassword()));
			logger.info("Password hashed for new operateur: {}", o.getNom());
		}
		operateurRepository.save(o);
		return o;
	}

	@Override
	public void deleteOperateur(Long id) {
		operateurRepository.deleteById(id);
	}

	/**
	 * OWASP A02:2021 – Cryptographic Failures
	 * Hash password on update only if a new plaintext password is provided.
	 */
	@Override
	public Operateur updateOperateur(Operateur o) {
		if (o.getPassword() != null && !o.getPassword().isEmpty()
				&& !o.getPassword().contains(":")) {
			// Only hash if it looks like a new plaintext password (no ":" separator)
			o.setPassword(PasswordHashUtil.hashPassword(o.getPassword()));
			logger.info("Password re-hashed for operateur: {}", o.getNom());
		}
		operateurRepository.save(o);
		return o;
	}

	@Override
	public Operateur retrieveOperateur(Long id) {
		Operateur operateur = operateurRepository.findById(id).orElse(null);
		return operateur;
	}

}
