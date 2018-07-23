package uk.co.bconline.ndelius.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransactionGroup {
    private String name;
    private List<Transaction> transactions;
}
