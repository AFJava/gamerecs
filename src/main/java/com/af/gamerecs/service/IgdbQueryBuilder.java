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

        for(int i = 0; i < Math.min(features.size(), 10); i++) {
            Feature feature = features.get(i);
            FeatureType type = feature.getFeatureType();

            if(! type.isExcludedFromMatching()) {
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
        }

        //params contains all &s at first
        StringBuilder params = new StringBuilder("(");
        StringBuilder ors = new StringBuilder("");

        for(FeatureType type : featureIdMap.keySet()) {
            StringBuilder param = new StringBuilder(type.toIgdbField() + " = (");

            for(int j = 0; j < featureIdMap.get(type).size(); j++) {
                if(j == 0) {
                    param.append(featureIdMap.get(type).get(j));
                } else {
                    param.append(", " + featureIdMap.get(type).get(j));
                }
            }

            param.append(")");

            //At this point param is "type = (#, ..., #)"
            if(type.shouldUseOrMatching()) {
                ors.append(param + " | ");
            }
            else {
                params.append(param + " & ");
            }
        }

        ors.setLength(ors.length() - 3);
        params.setLength(params.length() - 3);

        params.append(") | ");
        params.append(ors);

        //Format (ands) | (ors)
        System.out.println(params);

        return params.toString();
    }
}
