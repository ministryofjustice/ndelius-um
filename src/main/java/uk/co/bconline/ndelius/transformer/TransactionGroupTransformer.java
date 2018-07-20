package uk.co.bconline.ndelius.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.TransactionGroup;
import uk.co.bconline.ndelius.model.ldap.OIDTransactionGroup;

@Component
public class TransactionGroupTransformer
{
	private final DatasetTransformer datasetTransformer;

	@Autowired
	public TransactionGroupTransformer(DatasetTransformer datasetTransformer)
	{
		this.datasetTransformer = datasetTransformer;
	}

	public TransactionGroup map(OIDTransactionGroup transactionGroup){
		return TransactionGroup.builder()
				.transactionGroupName(transactionGroup.getName())
				.build();
	}
}
