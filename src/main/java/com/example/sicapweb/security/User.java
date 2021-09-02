package com.example.sicapweb.security;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class User {
    public String userName = "teste";
    public Date dateStart = Date.valueOf("2021-1-1");
    public Date dateEnd = Date.valueOf("2021-1-1");
    public List<String> systems = new ArrayList<>();


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

}
