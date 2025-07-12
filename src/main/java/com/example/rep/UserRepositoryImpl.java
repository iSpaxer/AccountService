package com.example.rep;

import com.example.dto.UserDto;
import com.example.dto.ViewsE;
import com.example.entity.StatusType;
import com.example.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryAdvanced {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserDto> findActiveByIdAndTypeView(Long id, ViewsE view) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDto> query = cb.createQuery(UserDto.class);
        Root<User> root = query.from(User.class);

        Predicate byId = cb.equal(root.get("id"), id);
        Predicate byStatus = cb.equal(root.get("status"), StatusType.ACTIVE);
        query.where(cb.and(byId, byStatus));

        switch (view) {
            case PUBLIC -> query.select(cb.construct(UserDto.class,
                                                     root.get("id"),
                                                     root.get("username"),
                                                     root.get("description")));

            case MYSELF -> query.select(cb.construct(UserDto.class,
                                                     root.get("id"),
                                                     root.get("status"),
                                                     root.get("createdDate"),
                                                     root.get("lastUpdateDate"),
                                                     root.get("username"),
                                                     root.get("description")));
        }

        try {
            UserDto result = entityManager.createQuery(query).getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
