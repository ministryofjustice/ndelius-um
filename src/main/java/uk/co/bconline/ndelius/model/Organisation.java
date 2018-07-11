package uk.co.bconline.ndelius.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Organisation {
    private String code;
    private String description;
}
