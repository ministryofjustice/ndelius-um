package uk.co.bconline.ndelius.util;

import org.springframework.ldap.odm.annotations.Entry;
import uk.co.bconline.ndelius.model.ldap.OIDRole;
import uk.co.bconline.ndelius.model.ldap.OIDUser;

public class Constants {
	// LDAP Bases
	public static final String ROLE_BASE = OIDRole.class.getAnnotation(Entry.class).base();
	public static final String USER_BASE = OIDUser.class.getAnnotation(Entry.class).base();

	// Business interactions
	public static final String PUBLIC_ACCESS = "UABI020";
	public static final String PRIVATE_ACCESS = "UABI021";
	public static final String LEVEL1_ACCESS = "UABI022";
	public static final String LEVEL2_ACCESS = "UABI023";
	public static final String LEVEL3_ACCESS = "UABI024";
	public static final String NATIONAL_ACCESS = "UABI025";
	public static final String LOCAL_ACCESS = "UABI026";

	// Roles
	public static final String NATIONAL_ROLE = "UABT0050";
}
