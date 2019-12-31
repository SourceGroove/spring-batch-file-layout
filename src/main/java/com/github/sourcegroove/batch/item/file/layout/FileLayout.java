package com.github.sourcegroove.batch.item.file.layout;

import java.util.List;

public interface FileLayout {
    List<RecordLayout> getRecordLayouts();
    int getLinesToSkip();
}
