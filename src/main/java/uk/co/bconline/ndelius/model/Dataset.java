package uk.co.bconline.ndelius.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
    private String code;
    private String description;
    private Organisation organisation;
    private boolean active;
}
