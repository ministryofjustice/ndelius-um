package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.TransactionGroup;
import uk.co.bconline.ndelius.model.ldap.OIDTransactionGroup;
import uk.co.bconline.ndelius.repository.oid.OIDRoleGroupRepository;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.transformer.TransactionGroupTransformer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

@Service
public class RoleGroupServiceImpl implements RoleGroupService {
    private final OIDRoleGroupRepository oidRoleGroupRepository;
    private final TransactionGroupTransformer transactionGroupTransformer;

    @Autowired
    public RoleGroupServiceImpl(OIDRoleGroupRepository oidRoleGroupRepository, TransactionGroupTransformer transactionGroupTransformer){
        this.oidRoleGroupRepository = oidRoleGroupRepository;
        this.transactionGroupTransformer = transactionGroupTransformer;
    }

    public Iterable<TransactionGroup> getTransactionGroups()
    {
        return stream(oidRoleGroupRepository
                .findAll(query()
                        .searchScope(ONELEVEL)
                        .base(OIDTransactionGroup.class.getAnnotation(Entry.class).base())
                        .where("objectclass").like("*")).spliterator(), false)
                .map(transactionGroupTransformer::map)
                .collect(toList());
    }
}
