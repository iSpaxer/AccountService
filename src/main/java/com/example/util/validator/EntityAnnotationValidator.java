package com.example.util.validator;

import com.example.entity.AbstractEntity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
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
                if (!entityClass.isAnnotationPresent(EntityListeners.class)) {
                    throw new IllegalStateException(entityClass.getName() + " must have @EntityListeners");
                }
            }
        }
    }


}