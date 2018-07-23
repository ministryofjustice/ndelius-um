package uk.co.bconline.ndelius.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TransactionGroup {
    private String name;
    private List<Transaction> transactions;
}
