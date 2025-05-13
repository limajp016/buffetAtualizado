package com.fateczl.BuffetRafaela.records;

import com.fateczl.BuffetRafaela.entities.Item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemQuantidade(
    @NotNull Item item,
    @NotNull @Min(1) Integer quantidade
) {}