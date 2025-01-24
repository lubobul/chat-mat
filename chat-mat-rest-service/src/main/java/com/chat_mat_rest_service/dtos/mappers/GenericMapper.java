package com.chat_mat_rest_service.dtos.mappers;

public interface GenericMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
}
