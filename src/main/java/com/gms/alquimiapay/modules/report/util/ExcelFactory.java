package com.gms.alquimiapay.modules.report.util;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

public interface ExcelFactory {

    String buildExcelDocumentReturnAbsPath(String workbookName, String sheet, Map<Integer, String> headers, List<List<String>> rowObject);
    String buildExcelDocumentFromListReturnAbsPath(String workbookName, String sheet, List<? extends Object> objects);

    @SneakyThrows
    String buildExcelDocumentFromSheetListReturnAbsPath(@NonNull String workbookName, @NonNull Map<String, List<? extends Object>> sheetAndItems);
}
