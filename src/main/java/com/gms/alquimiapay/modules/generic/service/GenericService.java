package com.gms.alquimiapay.modules.generic.service;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.generic.constant.HashAlgo;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@Service
public class GenericService implements IGenericService {


    @Override
    public String hashFileContent(File file, HashAlgo algo){
        if(algo.equals(HashAlgo.SHA_256)){
            ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
            HashCode hc = null;
            try {
                hc = byteSource.hash(Hashing.sha256());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return hc.toString();
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String getBinaryDataFromFile(File file){
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            in.read(fileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder content = new StringBuilder();
        for(byte b : fileData) {
            content.append(getBits(b));
        }
        return content.toString();
    }

    public String getBits(byte b)
    {
        String result = "";
        for(int i = 0; i < 8; i++)
            result += (b & (1 << i)) == 0 ? "0" : "1";
        return result;
    }
}
