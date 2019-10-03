package uk.co.bconline.ndelius.model.ldap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.*;

import javax.naming.Name;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = {"UserPreferences", "top"})
public final class OIDUserPreferences
{
	public OIDUserPreferences(String username)
	{
		this.username = username;
	}

	@Id
	private Name dn;

	@Attribute(name="cn")
	@DnAttribute(value="cn", index=1)
	private String cn = "UserPreferences";

	@Transient
	@DnAttribute(value="cn", index=0)
	private String username;

	@Attribute
	private String locale = "en";

	@Attribute
	private String contactLogDefaultStaff = "MANAGER";

	@Attribute
	private String showStaffGrade = "Y";

	@Attribute
	private String staffSortingType = "SURNAME";

	@Attribute
	private String bypassDiaryPicklist = "Y";

	@Attribute
	private String verboseHelp = "Y";

	@Attribute
	private String lastReferralDetails = "Y";

	@Attribute
	private String openLastOffender = "N";

	@Attribute
	private String mostRecentlyViewedOffenders = "NRO16";
}