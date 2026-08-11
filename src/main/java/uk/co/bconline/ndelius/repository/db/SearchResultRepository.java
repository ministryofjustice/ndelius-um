package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.SearchResultEntity;

import java.util.List;
import java.util.Set;

public interface SearchResultRepository extends JpaRepository<SearchResultEntity, String> {
    @Query(value = """
        select u.user_id||'-'||t.team_id as id,
            u.distinguished_name,
            u.forename,
            u.forename2,
            u.surname,
            u.end_date,
            s.officer_code as staff_code,
            t.code as team_code,
            t.description as team_description,
            case when ?1 is null then 0 else
            greatest(
                sys.utl_match.edit_distance_similarity(upper(u.distinguished_name), upper(?1)) / 100,
                sys.utl_match.edit_distance_similarity(upper(u.forename), upper(?1)) / 100,
                sys.utl_match.edit_distance_similarity(upper(u.forename2), upper(?1)) / 100,
                sys.utl_match.edit_distance_similarity(upper(u.surname), upper(?1)) / 100,
                case when upper(s.officer_code) like '%'||upper(?1)||'%' then length(?1)/length(s.officer_code) else 0 end,
                case when upper(t.code) like '%'||upper(?1)||'%' then length(?1)/length(t.code) else 0 end,
                case when upper(t.description) like '%'||upper(?1)||'%' then length(?1)/length(t.description) else 0 end
            ) end as score
        from user_ u
            left outer join staff s on u.staff_id = s.staff_id
            left outer join staff_team st on u.staff_id = st.staff_id
            left outer join team t on st.team_id = t.team_id
        where (?2 = 1 or u.end_date is null or u.end_date >= trunc(sysdate))
        and (?3 = 0 or (select count(*) from probation_area_user where rownum = 1 and user_id = u.user_id
            and probation_area_id in (select probation_area_id from probation_area where code in ?4)) > 0)
        and (?1 is null
            or soundex(u.distinguished_name) = soundex(?1)
            or soundex(u.forename) = soundex(?1)
            or soundex(u.forename2) = soundex(?1)
            or soundex(u.surname) = soundex(?1)
            or upper(s.officer_code) like '%'||upper(?1)||'%'
            or s.staff_id in (select staff_id from staff_team where team_id in (
                select team_id from team
                where upper(code) like '%'||upper(?1)||'%'
                or upper(description) like '%'||upper(?1)||'%')))
        order by score desc, upper(u.distinguished_name)
        """,
        nativeQuery = true)
    List<SearchResultEntity> search(String query, boolean includeInactiveUsers, boolean filterDatasets, Set<String> datasetCodes);

    @Query(value = """
        select u.user_id||'-'||t.team_id as id,
            u.distinguished_name,
            u.forename,
            u.forename2,
            u.surname,
            u.end_date,
            s.officer_code as staff_code,
            t.code as team_code,
            t.description as team_description,
            coalesce(greatest(
                length(?1)/length(u.distinguished_name),
                case when u.forename is null or length(u.forename)=0 then 0 else length(?1)/length(u.forename) end,
                case when u.forename2 is null or length(u.forename2)=0 then 0 else length(?1)/length(u.forename2) end,
                case when u.surname is null or length(u.surname)=0 then 0 else length(?1)/length(u.surname) end,
                case when s.officer_code is null or length(s.officer_code)=0 then 0 else length(?1)/length(s.officer_code) end,
                case when t.code is null or length(t.code)=0 then 0 else length(?1)/length(t.code) end,
                case when t.description is null or length(t.description)=0 then 0 else length(?1)/length(t.description) end
            ), 0) as score
        from user_ u
            left outer join staff s on u.staff_id = s.staff_id
            left outer join staff_team st on u.staff_id = st.staff_id
            left outer join team t on st.team_id = t.team_id
        where (?2 = 1 or u.end_date is null or u.end_date >= trunc(sysdate))
        and (?3 = 0 or (select count(*) from probation_area_user where rownum = 1 and user_id = u.user_id
            and probation_area_id in (select probation_area_id from probation_area where code in (?4))) > 0)
        and (?1 = ''
            or upper(u.distinguished_name) like '%'||upper(?1)||'%'
            or upper(u.forename) like '%'||upper(?1)||'%'
            or upper(u.forename2) like '%'||upper(?1)||'%'
            or upper(u.surname) like '%'||upper(?1)||'%'
            or upper(s.officer_code) like '%'||upper(?1)||'%'
            or s.staff_id in (select staff_id from staff_team where team_id in (
                select team_id from team
                where upper(code) like '%'||upper(?1)||'%'
                or upper(description) like '%'||upper(?1)||'%')))
        order by score desc, upper(u.distinguished_name);
        """,
        nativeQuery = true)
    List<SearchResultEntity> simpleSearch(String query, int includeInactiveUsers, int filterDatasets, Set<String> datasetCodes);
}
