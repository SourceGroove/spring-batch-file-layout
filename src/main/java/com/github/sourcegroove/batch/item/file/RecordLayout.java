package com.github.sourcegroove.batch.item.file;

import java.util.List;

public interface RecordLayout {
    
    String getType();
    List<ColumnLayout> getColumns();
}
