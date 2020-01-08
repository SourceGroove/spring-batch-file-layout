package com.github.sourcegroove.batch.item.file.layout;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public interface FileLayout {
    List<RecordLayout> getRecordLayouts();
    int getLinesToSkip();

    default boolean isValid(){
        return CollectionUtils.isNotEmpty(getRecordLayouts())
                && !getRecordLayouts()
                    .stream()
                    .filter(r -> r.getTargetType() == null)
                    .findFirst()
                    .isPresent();
    }

    default RecordLayout getRecordLayout(Class targetType){
        return getRecordLayouts()
                .stream()
                .filter(r -> r.getTargetType() == targetType)
                .findFirst()
                .orElse(null);
    }
}
