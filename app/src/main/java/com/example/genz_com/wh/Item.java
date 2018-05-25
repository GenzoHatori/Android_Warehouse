package com.example.genz_com.wh;

/**
 * Created by Genz_Com on 2017/10/05.
 */

public class Item {
    public String Id;
    public String SerialNo;
    public String ItemNMBR;
    public String ItemDesC;
    public String Act_GrossWT;
    public String Status;
    public String Location;
    public String WareHouse;
    public String ModDate;

    public Item(){

    }

    public Item(String id, String warehouse, String serialNo, String itemnmbr, String itemdesc, String act_GrossWT, String status, String location){
        Id = id;
        WareHouse = warehouse;
        SerialNo = serialNo;
        ItemNMBR = itemnmbr;
        ItemDesC = itemdesc;
        Act_GrossWT = act_GrossWT;
        Status = status;
        Location = location;
    }
}
