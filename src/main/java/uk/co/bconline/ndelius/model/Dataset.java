package uk.co.bconline.ndelius.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dataset {
    private Long id;
    private String code;
    private String description;
    private Organisation organisation;
}
