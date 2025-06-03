package com.example.PostService.util.validator;

import com.example.PostService.entity.AbstractEntity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

//@Component
public class EntityAnnotationValidator implements ApplicationRunner {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public EntityAnnotationValidator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void run(ApplicationArguments args) {
        var metamodel = entityManagerFactory.getMetamodel();

        for (EntityType<?> entityType : metamodel.getEntities()) {
            Class<?> entityClass = entityType.getJavaType();

            if (AbstractEntity.class.isAssignableFrom(entityClass)) {
                if (!entityClass.isAnnotationPresent(SQLRestriction.class)
                    && !entityClass.isAnnotationPresent(SQLDelete.class)) {
                    throw new IllegalStateException(entityClass.getName() + " must have @SQLRestriction");
                }
            }
        }
    }



}