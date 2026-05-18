package tn.esprit.rh.achat.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Operateur implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idOperateur;

	/* OWASP A03 – Input Validation */
	@NotBlank(message = "Le nom est obligatoire")
	@Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
	private String nom;

	@NotBlank(message = "Le prénom est obligatoire")
	@Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
	private String prenom;

	/* OWASP A02 – write-only: password is accepted in JSON but never returned */
	@NotBlank(message = "Le mot de passe est obligatoire")
	@Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@OneToMany
	@JsonIgnore
	private Set<Facture> factures;
	
}
