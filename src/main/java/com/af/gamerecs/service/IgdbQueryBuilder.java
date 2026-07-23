package com.af.gamerecs.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.FeatureType;
import com.af.gamerecs.entities.Feature;

@Service
public class IgdbQueryBuilder {
    public final CompanyReferenceService companyReferenceService;

    public IgdbQueryBuilder(CompanyReferenceService companyReferenceService) {
        this.companyReferenceService = companyReferenceService;
    }

    public String parseParams(List<Feature> features) {
        HashMap<FeatureType, List<Long>> featureIdMap = new HashMap<>();

        for(Feature feature : features) {
            FeatureType type = feature.getFeatureType();
            
            if(! featureIdMap.containsKey(type)) {
                featureIdMap.put(type, new ArrayList<Long>());
            }

            if(! type.isCompany()) {
                featureIdMap.get(type).add(feature.getIgdbFeatureId());
            }
            else {
                featureIdMap.get(type).addAll(
                    companyReferenceService.getAllInvolvedCompanyIds(feature.getIgdbFeatureId(), type)
                );
            }
        }

        StringBuilder params = new StringBuilder("");

        for(FeatureType type : featureIdMap.keySet()) {
            StringBuilder param = new StringBuilder(type.toIgdbField() + " = (");

            for(int i = 0; i < featureIdMap.get(type).size(); i++) {
                if(i == 0) {
                    param.append(featureIdMap.get(type).get(i));
                } else {
                    param.append(", " + featureIdMap.get(type).get(i));
                }
            }

            param.append(")");
            params.append(param + " | ");
        }

        params.setLength(params.length() - 3);

        System.out.println(params);

        return params.toString();
    }
}
