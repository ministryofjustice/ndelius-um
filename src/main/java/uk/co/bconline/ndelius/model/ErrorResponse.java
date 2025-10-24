package uk.co.bconline.ndelius.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static java.util.Collections.singletonList;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    public ErrorResponse(String message) {
        this.error = singletonList(message);
    }

    private List<String> error;
}
