package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;

import java.util.List;
import java.util.Set;

public interface SearchResultRepository extends JpaRepository<SearchResultEntity, String>
{
	@Query(value = "SELECT U.USER_ID||'-'||T.TEAM_ID AS ID, " +
			"    U.DISTINGUISHED_NAME, " +
			"    U.FORENAME, " +
			"    U.FORENAME2, " +
			"    U.SURNAME, " +
			"    U.END_DATE, " +
			"    S.OFFICER_CODE AS STAFF_CODE, " +
			"    T.CODE AS TEAM_CODE, " +
			"    T.DESCRIPTION AS TEAM_DESCRIPTION, " +
			"    CASE WHEN ?1 IS NULL THEN 0 ELSE " +
			"    GREATEST( " +
			"        SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(UPPER(U.DISTINGUISHED_NAME), UPPER(?1)) / 100, " +
			"        SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(UPPER(U.FORENAME), UPPER(?1)) / 100, " +
			"        SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(UPPER(U.FORENAME2), UPPER(?1)) / 100, " +
			"        SYS.UTL_MATCH.EDIT_DISTANCE_SIMILARITY(UPPER(U.SURNAME), UPPER(?1)) / 100, " +
			"        CASE WHEN UPPER(S.OFFICER_CODE) LIKE '%'||UPPER(?1)||'%' THEN LENGTH(?1)/LENGTH(S.OFFICER_CODE) ELSE 0 END, " +
			"        CASE WHEN UPPER(T.CODE) LIKE '%'||UPPER(?1)||'%' THEN LENGTH(?1)/LENGTH(T.CODE) ELSE 0 END, " +
			"        CASE WHEN UPPER(T.DESCRIPTION) LIKE '%'||UPPER(?1)||'%' THEN LENGTH(?1)/LENGTH(T.DESCRIPTION) ELSE 0 END " +
			"    ) END AS SCORE " +
			"FROM USER_ U " +
			"    LEFT OUTER JOIN STAFF S ON U.STAFF_ID = S.STAFF_ID " +
			"    LEFT OUTER JOIN STAFF_TEAM ST ON U.STAFF_ID = ST.STAFF_ID " +
			"    LEFT OUTER JOIN TEAM T ON ST.TEAM_ID = T.TEAM_ID " +
			"WHERE (?2 = 1 OR U.END_DATE IS NULL OR U.END_DATE >= TRUNC(SYSDATE)) " +
			"AND (?3 = 0 OR (SELECT COUNT(*) FROM PROBATION_AREA_USER WHERE ROWNUM = 1 AND USER_ID = U.USER_ID  " +
			"    AND PROBATION_AREA_ID IN (SELECT PROBATION_AREA_ID FROM PROBATION_AREA WHERE CODE IN ?4)) > 0) " +
			"AND (?1 IS NULL " +
			"    OR SOUNDEX(U.DISTINGUISHED_NAME) = SOUNDEX(?1) " +
			"    OR SOUNDEX(U.FORENAME) = SOUNDEX(?1) " +
			"    OR SOUNDEX(U.FORENAME2) = SOUNDEX(?1) " +
			"    OR SOUNDEX(U.SURNAME) = SOUNDEX(?1) " +
			"    OR UPPER(S.OFFICER_CODE) LIKE '%'||UPPER(?1)||'%' " +
			"    OR S.STAFF_ID IN (SELECT STAFF_ID FROM STAFF_TEAM WHERE TEAM_ID IN ( " +
			"        SELECT TEAM_ID FROM TEAM  " +
			"        WHERE UPPER(CODE) LIKE '%'||UPPER(?1)||'%' " +
			"        OR UPPER(DESCRIPTION) LIKE '%'||UPPER(?1)||'%'))) " +
			"ORDER BY SCORE DESC, UPPER(U.DISTINGUISHED_NAME)",
			nativeQuery = true)
	List<SearchResultEntity> search(String query, boolean includeInactiveUsers, boolean filterDatasets, Set<String> datasetCodes);

	@Query(value = "SELECT U.USER_ID||'-'||T.TEAM_ID AS ID," +
			"    U.DISTINGUISHED_NAME, " +
			"    U.FORENAME, " +
			"    U.FORENAME2, " +
			"    U.SURNAME, " +
			"    U.END_DATE, " +
			"    S.OFFICER_CODE AS STAFF_CODE, " +
			"    T.CODE AS TEAM_CODE, " +
			"    T.DESCRIPTION AS TEAM_DESCRIPTION, " +
			"    GREATEST( " +
			"        LENGTH(?1)/LENGTH(U.DISTINGUISHED_NAME), " +
			"        CASE WHEN U.FORENAME IS NULL OR LENGTH(U.FORENAME)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(U.FORENAME) END, " +
			"        CASE WHEN U.FORENAME2 IS NULL OR LENGTH(U.FORENAME2)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(U.FORENAME2) END, " +
			"        CASE WHEN U.SURNAME IS NULL OR LENGTH(U.SURNAME)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(U.SURNAME) END, " +
			"        CASE WHEN S.OFFICER_CODE IS NULL OR LENGTH(S.OFFICER_CODE)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(S.OFFICER_CODE) END, " +
			"        CASE WHEN T.CODE IS NULL OR LENGTH(T.CODE)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(T.CODE) END, " +
			"        CASE WHEN T.DESCRIPTION IS NULL OR LENGTH(T.DESCRIPTION)=0 THEN 0 ELSE LENGTH(?1)/LENGTH(T.DESCRIPTION) END " +
			"    ) AS SCORE " +
			"FROM USER_ U " +
			"    LEFT OUTER JOIN STAFF S ON U.STAFF_ID = S.STAFF_ID " +
			"    LEFT OUTER JOIN STAFF_TEAM ST ON U.STAFF_ID = ST.STAFF_ID " +
			"    LEFT OUTER JOIN TEAM T ON ST.TEAM_ID = T.TEAM_ID " +
			"WHERE (?2 = 1 OR U.END_DATE IS NULL OR U.END_DATE >= TRUNC(SYSDATE)) " +
			"AND (?3 = 0 OR (SELECT COUNT(*) FROM PROBATION_AREA_USER WHERE ROWNUM = 1 AND USER_ID = U.USER_ID  " +
			"    AND PROBATION_AREA_ID IN (SELECT PROBATION_AREA_ID FROM PROBATION_AREA WHERE CODE IN (?4))) > 0) " +
			"AND (?1 = '' " +
			"    OR UPPER(U.DISTINGUISHED_NAME) LIKE '%'||UPPER(?1)||'%' " +
			"    OR UPPER(U.FORENAME) LIKE '%'||UPPER(?1)||'%' " +
			"    OR UPPER(U.FORENAME2) LIKE '%'||UPPER(?1)||'%' " +
			"    OR UPPER(U.SURNAME) LIKE '%'||UPPER(?1)||'%' " +
			"    OR UPPER(S.OFFICER_CODE) LIKE '%'||UPPER(?1)||'%' " +
			"    OR S.STAFF_ID IN (SELECT STAFF_ID FROM STAFF_TEAM WHERE TEAM_ID IN ( " +
			"        SELECT TEAM_ID FROM TEAM  " +
			"        WHERE UPPER(CODE) LIKE '%'||UPPER(?1)||'%' " +
			"        OR UPPER(DESCRIPTION) LIKE '%'||UPPER(?1)||'%'))) " +
			"ORDER BY SCORE DESC, UPPER(U.DISTINGUISHED_NAME);",
			nativeQuery = true)
	List<SearchResultEntity> simpleSearch(String query, int includeInactiveUsers, int filterDatasets, Set<String> datasetCodes);
}
