package uk.co.bconline.ndelius.model.ldap;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entry(objectClasses = {"UserPreferences", "top"}, base = "cn=Users")
public final class OIDUserPreferences
{
	public OIDUserPreferences(String username)
	{
		this.username = username;
	}

	@Id
	private Name dn;

	@Attribute(name="cn")
	@DnAttribute(value="cn", index=2)
	private String cn = "UserPreferences";

	@Transient
	@DnAttribute(value="cn", index=1)
	private String username;

	@Attribute
	private String area = "";

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