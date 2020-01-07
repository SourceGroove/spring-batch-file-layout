package com.github.sourcegroove.batch.item.file.model;

import java.util.List;

public interface FileLayout {
    List<RecordLayout> getRecordLayouts();
    int getLinesToSkip();

    default RecordLayout getRecordLayout(Class targetType){
        return getRecordLayouts()
                .stream()
                .filter(r -> r.getTargetType() == targetType)
                .findFirst()
                .orElse(null);
    }
}
