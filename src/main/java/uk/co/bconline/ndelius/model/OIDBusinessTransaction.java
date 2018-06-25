package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entry(objectClasses = "top")
public final class OIDBusinessTransaction implements Serializable
{
	@Id
	@JsonIgnore
	private Name dn;

	@Attribute(name="cn")
	private String name;

	@Attribute(name="aliasedObjectName")
	private String aliasedObjectName;

	@Attribute(name="UIBusinessInteraction")
	private List<String> roles;
}