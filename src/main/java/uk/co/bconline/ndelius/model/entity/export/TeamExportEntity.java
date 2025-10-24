package uk.co.bconline.ndelius.model.entity.export;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

import static java.time.LocalDate.now;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
public class TeamExportEntity implements Serializable {
    @Id
    @Column(name = "TEAM_ID")
    @GeneratedValue(generator = "TEAM_ID_SEQ")
    @SequenceGenerator(name = "TEAM_ID_SEQ", sequenceName = "TEAM_ID_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "DISTRICT_ID")
    private DistrictExportEntity district;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    public String getExportDescription() {
        return description + " (" + code + ")" + ((getEndDate() != null && getEndDate().isBefore(now())) ? " [Inactive]" : "");
    }
}
