package com.gms.alquimiapay.modules.report.util;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.report.annotation.ExcelHeader;
import com.gms.alquimiapay.modules.report.test.ExcelTest;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is a generic Non Spring Bean class that handles the filling of Excel sheets.
 */
public class SimpleExcelFactory implements ExcelFactory
{

    private static final int NON_ROW_OFFSET = 2;

    private SimpleExcelFactory(){

    }

    public static SimpleExcelFactory getInstance(){
        return new SimpleExcelFactory();
    }

    @Override
    public String buildExcelDocumentReturnAbsPath(@NonNull String workbookName, @NonNull String sheet, Map<Integer, String> headers, List<List<String>> rowObject){
        // Create the Workbook and Sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet workSheet = getOrCreateSheet(sheet, workbook);

        // Create the Header and its style.
        CellStyle cellStyle = getHeaderStyle(workbook);
        Row headerRow = workSheet.createRow(0);
        for(Map.Entry<Integer, String> header : headers.entrySet()){
            Cell headerCell = headerRow.createCell(header.getKey());
            headerCell.setCellValue(header.getValue());
            headerCell.setCellStyle(cellStyle);
        }

        // Fill up the remaining rows.
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        int rowStartOffset = NON_ROW_OFFSET;
        for(List<String> rowItems : rowObject){
            Row nextRow = workSheet.createRow(rowStartOffset);
            int colStartOffset = 0;
            for(String colItem : rowItems){
                Cell cell = nextRow.createCell(colStartOffset);
                cell.setCellValue(colItem);
                cell.setCellStyle(style);
                colStartOffset++;
            }
            rowStartOffset++;
        }

        // Create a File to represent the Workbook and return the absolute path
        File currentDir = new File(".");
        String workbookPath = currentDir.getAbsolutePath().concat(File.separator).concat("excel.xlsx");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(workbookPath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbookPath;
    }

    @SneakyThrows
    @Override
    public String buildExcelDocumentFromListReturnAbsPath(@NonNull String workbookName, @NonNull String sheet, List<? extends Object> objects) {

        // Create the Workbook and Sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet workSheet = getOrCreateSheet(sheet, workbook);

        // Create the Header and its style.
        CellStyle cellStyle = getHeaderStyle(workbook);
        Row headerRow = workSheet.createRow(0);
        List<String> headers = getHeadersOfExcelWorkbookFromObjectType(objects.get(0));
        for(int i = 0; i < headers.size(); i++){
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers.get(i));
            headerCell.setCellStyle(cellStyle);
        }

        // Fill the other columns
        CellStyle style = workbook.createCellStyle();
        style.setLocked(true);
        int rowStartOffset = NON_ROW_OFFSET;
        for(Object object : objects){
            Row row = workSheet.createRow(rowStartOffset);
            Field[] fields = object.getClass().getDeclaredFields();

            int columnOffset = 0;
            for(Field field : fields){
                field.setAccessible(true);
                String fieldValue = String.valueOf(field.get(object));
                 Cell cell = row.createCell(columnOffset);
                 cell.setCellValue(fieldValue);
                 cell.setCellStyle(style);
                 workSheet.autoSizeColumn(columnOffset, true);
                 columnOffset++;
            }
            rowStartOffset++;
        }

        // Create a File to represent the Workbook and return the absolute path
        File currentDir = new File(".");
        String workbookPath = currentDir.getAbsolutePath().concat(File.separator).concat(workbookName.concat(".xlsx"));
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(workbookPath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbookPath;
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("DuplicatedCode")
    public String buildExcelDocumentFromSheetListReturnAbsPath(@NonNull String workbookName, @NonNull Map<String, List<? extends Object>> sheetAndItems){
        // Create the Workbook and Sheet
        Workbook workbook = new XSSFWorkbook();

        for(Map.Entry<String, List<? extends Object>> entry : sheetAndItems.entrySet()) {
            String sheet = entry.getKey();
            List<? extends Object> objects = entry.getValue();

            Sheet workSheet = getOrCreateSheet(sheet, workbook);

            // Create the Header and its style.
            CellStyle cellStyle = getHeaderStyle(workbook);
            Row headerRow = workSheet.createRow(0);
            List<String> headers = getHeadersOfExcelWorkbookFromObjectType(objects.get(0));
            for (int i = 0; i < headers.size(); i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers.get(i));
                headerCell.setCellStyle(cellStyle);
            }

            // Fill the other columns
            CellStyle style = workbook.createCellStyle();
            style.setLocked(true);
            style.setAlignment(HorizontalAlignment.CENTER);
            int rowStartOffset = NON_ROW_OFFSET;
            for (Object object : objects) {
                Row row = workSheet.createRow(rowStartOffset);
                Field[] fields = object.getClass().getDeclaredFields();

                int columnOffset = 0;
                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldValue = String.valueOf(field.get(object));
                    Cell cell = row.createCell(columnOffset);
                    cell.setCellValue(fieldValue);
                    cell.setCellStyle(style);
                    workSheet.autoSizeColumn(columnOffset, true);
                    columnOffset++;
                }
                rowStartOffset++;
            }
        }

        // Create a File to represent the Workbook and return the absolute path
        File currentDir = new File(".");
        String workbookPath = currentDir.getAbsolutePath().concat(File.separator).concat(workbookName.concat(".xlsx"));
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(workbookPath);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbookPath;
    }


    private List<String> getHeadersOfExcelWorkbookFromObjectType(Object object){
        return Arrays.stream(object.getClass().getDeclaredFields())
                .map(field -> {
                    field.setAccessible(true);
                    return field.isAnnotationPresent(ExcelHeader.class) ?
                            (field.getAnnotation(ExcelHeader.class).name().equalsIgnoreCase(StringValues.EMPTY_STRING) ?
                                    field.getName().toUpperCase() :
                                    field.getAnnotation(ExcelHeader.class).name()) :
                            field.getName().toUpperCase();
                })
                .collect(Collectors.toList());
    }


    private CellStyle getHeaderStyle(Workbook workbook){
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);

        return headerStyle;
    }

    private Sheet getOrCreateSheet(String sheet, Workbook workbook){
        return workbook.getSheet(sheet) == null ? workbook.createSheet(sheet) : workbook.getSheet(sheet);
    }

    public static String testExcel(){
        SimpleExcelFactory factory = new SimpleExcelFactory();
        ExcelTest test1 = ExcelTest.builder().age(12L).date(LocalDate.now()).isMarried(true).name("Sunday Lucky Enyinna").build();
        ExcelTest test2 = ExcelTest.builder().age(14L).date(LocalDate.now()).isMarried(true).name("Sunday Lucky Roger").build();
        ExcelTest test3 = ExcelTest.builder().age(13L).date(LocalDate.now()).isMarried(false).name("Sunday Lucky Master").build();

        List<ExcelTest> excelTests = Arrays.asList(test1, test2, test3);

        return factory.buildExcelDocumentFromListReturnAbsPath("Workbook", "Names", excelTests);
    }

}
