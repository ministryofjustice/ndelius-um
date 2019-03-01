package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;

import java.util.List;

public interface SearchResultRepository extends JpaRepository<SearchResultEntity, String>
{
	@Query(value = "SELECT U.DISTINGUISHED_NAME,"
			+ "  MAX(GREATEST(SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(LOWER(U.DISTINGUISHED_NAME), LOWER(?1)) / 100,"
			+ "               SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(LOWER(U.FORENAME), LOWER(?1)) / 100,"
			+ "               SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(LOWER(U.FORENAME2), LOWER(?1)) / 100,"
			+ "               SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(LOWER(U.SURNAME), LOWER(?1)) / 100,"
			+ "               CASE WHEN LOWER(S.OFFICER_CODE) LIKE '%' || LOWER(?1) || '%'"
			+ "                    THEN LENGTH(?1)/LENGTH(S.OFFICER_CODE) ELSE 0 END,"
			+ "               CASE WHEN LOWER(T.CODE) LIKE '%' || LOWER(?1) || '%'"
			+ "                    THEN LENGTH(?1)/LENGTH(T.CODE) ELSE 0 END,"
			+ "               CASE WHEN LOWER(T.DESCRIPTION) LIKE '%' || LOWER(?1) || '%' "
			+ "                    THEN LENGTH(?1)/LENGTH(T.DESCRIPTION) ELSE 0 END)"
			+ "  ) AS SCORE"
			+ " FROM USER_ U"
			+ "  LEFT OUTER JOIN STAFF S ON U.STAFF_ID = S.STAFF_ID"
			+ "  LEFT OUTER JOIN STAFF_TEAM ST ON U.STAFF_ID = ST.STAFF_ID"
			+ "  LEFT OUTER JOIN TEAM T ON ST.TEAM_ID = T.TEAM_ID"
			+ " WHERE (?2 OR U.END_DATE IS NULL OR U.END_DATE >= SYSDATE)"
			+ " AND (SOUNDEX(U.DISTINGUISHED_NAME) = SOUNDEX(?1)"
			+ "      OR SOUNDEX(U.FORENAME) = SOUNDEX(?1)"
			+ "      OR SOUNDEX(U.FORENAME2) = SOUNDEX(?1)"
			+ "      OR SOUNDEX(U.SURNAME) = SOUNDEX(?1)"
			+ "      OR LOWER(S.OFFICER_CODE) LIKE '%' || LOWER(?1) || '%'"
			+ "      OR LOWER(T.CODE) LIKE '%' || LOWER(?1) || '%'"
			+ "      OR LOWER(T.DESCRIPTION) LIKE '%' || LOWER(?1) || '%')"
			+ " GROUP BY U.DISTINGUISHED_NAME"
			+ " ORDER BY SCORE DESC",
		   nativeQuery = true)
	List<SearchResultEntity> search(String query, boolean includeInactiveUsers);

	@Query(value = "SELECT U.DISTINGUISHED_NAME, "
			+ "  MAX(GREATEST( "
			+ "      LENGTH(?1)/LENGTH(U.DISTINGUISHED_NAME), "
			+ "      CASE WHEN U.FORENAME IS NULL OR LENGTH(U.FORENAME)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(U.FORENAME) END, "
			+ "      CASE WHEN U.FORENAME2 IS NULL OR LENGTH(U.FORENAME2)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(U.FORENAME2) END, "
			+ "      CASE WHEN U.SURNAME IS NULL OR LENGTH(U.SURNAME)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(U.SURNAME) END, "
			+ "      CASE WHEN S.OFFICER_CODE IS NULL OR LENGTH(S.OFFICER_CODE)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(S.OFFICER_CODE) END, "
			+ "      CASE WHEN T.CODE IS NULL OR LENGTH(T.CODE)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(T.CODE) END, "
			+ "      CASE WHEN T.DESCRIPTION IS NULL OR LENGTH(T.DESCRIPTION)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(T.DESCRIPTION) END)) AS SCORE "
			+ "FROM USER_ U "
			+ "      LEFT OUTER JOIN STAFF S on U.STAFF_ID = S.STAFF_ID "
			+ "      LEFT OUTER JOIN STAFF_TEAM ST on U.STAFF_ID = ST.STAFF_ID "
			+ "      LEFT OUTER JOIN TEAM T ON ST.TEAM_ID = T.TEAM_ID "
			+ "WHERE (?2 OR U.END_DATE IS NULL OR U.END_DATE >= SYSDATE) "
			+ "AND (LOWER(U.DISTINGUISHED_NAME) LIKE '%'||LOWER(?1)||'%' "
			+ "      OR LOWER(U.FORENAME) LIKE '%'||LOWER(?1)||'%' "
			+ "      OR LOWER(U.FORENAME2) LIKE '%'||LOWER(?1)||'%' "
			+ "      OR LOWER(U.SURNAME) LIKE '%'||LOWER(?1)||'%' "
			+ "      OR LOWER(S.OFFICER_CODE) LIKE '%'||LOWER(?1)||'%' "
			+ "      OR LOWER(T.CODE) LIKE '%'||LOWER(?1)||'%' "
			+ "      OR LOWER(T.DESCRIPTION) LIKE '%'||LOWER(?1)||'%') "
			+ "GROUP BY DISTINGUISHED_NAME "
			+ "ORDER BY SCORE DESC", nativeQuery = true)
	List<SearchResultEntity> simpleSearch(String query, boolean includeInactiveUsers);
}
