package com.gms.alquimiapay.model;

import lombok.Data;

@Data
public class LookupData
{
    private String id;
    private String name;
    private String label;
    private boolean requiresCertification;
}
