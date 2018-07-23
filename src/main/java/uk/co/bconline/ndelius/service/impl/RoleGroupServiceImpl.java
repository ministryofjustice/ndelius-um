package uk.co.bconline.ndelius.service.impl;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.TransactionGroup;
import uk.co.bconline.ndelius.model.ldap.OIDTransactionGroup;
import uk.co.bconline.ndelius.repository.oid.OIDRoleGroupRepository;
import uk.co.bconline.ndelius.service.RoleGroupService;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.transformer.TransactionGroupTransformer;

import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.ldap.query.SearchScope.ONELEVEL;

@Service
public class RoleGroupServiceImpl implements RoleGroupService {
    private final OIDRoleGroupRepository oidRoleGroupRepository;
    private final TransactionGroupTransformer transactionGroupTransformer;
    private final RoleService roleService;
    @Autowired
    public RoleGroupServiceImpl(OIDRoleGroupRepository oidRoleGroupRepository, TransactionGroupTransformer transactionGroupTransformer, RoleService roleService){
        this.oidRoleGroupRepository = oidRoleGroupRepository;
        this.transactionGroupTransformer = transactionGroupTransformer;
        this.roleService = roleService;
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

    @Override
    public Optional<TransactionGroup> getTransactionGroup(String transactionGroupName) {
        val group = oidRoleGroupRepository.findByName(transactionGroupName);
        return group.map(g -> {
            g.setTransactions(roleService.getTransactionsByParent(transactionGroupName, OIDTransactionGroup.class));
            return g;
        }).map(transactionGroupTransformer::map);
    }
}
