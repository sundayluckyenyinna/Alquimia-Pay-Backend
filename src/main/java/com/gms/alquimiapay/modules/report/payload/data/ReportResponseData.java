package com.gms.alquimiapay.modules.report.payload.data;

import lombok.Data;

import java.util.List;

@Data
public class ReportResponseData<T>
{
    List<T> data;
    private String startDateTime;
    private String endDateTime;
    private Integer recordCount;
}
