package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Transaction;
import uk.co.bconline.ndelius.model.TransactionGroup;
import uk.co.bconline.ndelius.model.ldap.OIDTransactionGroup;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Component
public class TransactionGroupTransformer
{
	public TransactionGroup map(OIDTransactionGroup transactionGroup){
		return TransactionGroup.builder()
				.name(transactionGroup.getName())
				.transactions(ofNullable(transactionGroup.getTransactions())
						.map(list -> list.stream()
							.map(t -> Transaction.builder()
									.name(t.getName())
									.description(t.getDescription()).build())
							.collect(toList()))
						.orElse(null))
				.build();
	}
}
