package com.af.gamerecs.dto;

public record CompanyDto(Long id,
                        FeatureDto company,
                        Boolean developer,
                        Boolean publisher,
                        Boolean supporting,
                        Boolean porting) {

}
