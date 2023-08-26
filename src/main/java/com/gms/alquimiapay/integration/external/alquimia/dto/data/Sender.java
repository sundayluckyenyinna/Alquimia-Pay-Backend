package com.gms.alquimiapay.integration.external.alquimia.dto.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sender
{
    private String pais;
    private AdditionalDetails d;
    @SerializedName("Id") private Integer id;
    @SerializedName("FirstName") private String firstName;
    @SerializedName("SecondName") private String secondName;
    @SerializedName("LastName") private String lastName;
    @SerializedName("SecondLastName") private String secondLastName;
    @SerializedName("HomePhone") private String homePhone;
    @SerializedName("WorkPhone") private String workPhone;
    @SerializedName("Address") private String address;
    @SerializedName("Gender") private String gender;
    @SerializedName("Birthday") private String birthday;
    @SerializedName("City") private String city;
    @SerializedName("State") private String state;
    @SerializedName("Email") private String email;
}
