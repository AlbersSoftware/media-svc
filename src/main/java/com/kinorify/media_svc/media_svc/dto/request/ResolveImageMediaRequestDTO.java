package com.kinorify.media_svc.media_svc.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolveImageMediaRequestDTO {

    @NotEmpty
    private List<@NotNull UUID> mediaIds;
}
