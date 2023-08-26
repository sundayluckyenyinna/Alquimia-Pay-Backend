package com.gms.alquimiapay.modules.generic.controller;

import com.gms.alquimiapay.modules.generic.constant.GenericApiPath;
import com.gms.alquimiapay.modules.generic.payload.LookupDataResponsePayload;
import com.gms.alquimiapay.modules.generic.service.IGenericService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GenericApiPath.GENERIC_PATH_BASE)
@Api(tags = "Generic service", description = "A complete generic service for generic implementations and lookups")
public class GenericRestController
{

    private final IGenericService genericService;

    @Autowired
    public GenericRestController(IGenericService genericService) {
        this.genericService = genericService;
    }

}
