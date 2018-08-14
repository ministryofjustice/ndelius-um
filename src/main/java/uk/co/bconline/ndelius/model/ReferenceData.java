package uk.co.bconline.ndelius.model;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceData
{
    @NotBlank private String code;
    private String description;
}
