package com.gms.alquimiapay.modules.report.annotation;

import com.gms.alquimiapay.constants.StringValues;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelHeader
{
    String name() default StringValues.EMPTY_STRING;
}
