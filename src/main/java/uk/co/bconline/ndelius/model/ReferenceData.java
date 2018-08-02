package uk.co.bconline.ndelius.model;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceData
{
    private String code;
    private String description;
}
