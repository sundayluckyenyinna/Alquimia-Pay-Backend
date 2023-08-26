package com.gms.alquimiapay.modules.report.test;

import com.gms.alquimiapay.modules.report.annotation.ExcelHeader;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ExcelTest {

    @ExcelHeader(name = "Name")
    private String name;

    @ExcelHeader(name = "Age")
    private Long age;

    @ExcelHeader(name = "Date")
    private LocalDate date;

    @ExcelHeader(name = "Is Married")
    private boolean isMarried;

}
