package com.capstone.project.domain;

public class Account {
    private long  accountnumber;
    private long customerid;
    private String accounttype;
    private String branch;

    public Account(long accountnumber,long customerid, String branch, String accounttype) {
        this.accountnumber = accountnumber;
        this.customerid = customerid;
        this.accounttype = accounttype;
        this.branch=branch;
    }

    public Account() {
    }

    public long getaccountnumber() {
        return accountnumber;
    }

    public void setaccountnumber(long accountnumber) {
        this.accountnumber = accountnumber;
    }
    public long getcustomerid() {
        return customerid;
    }

    public void setcustomerid(long customerid) {
        this.customerid = customerid;
    }
    public String getaccounttype() {
        return accounttype;
    }

    public void setaccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getbranch() {
        return branch;
    }

    public void setbranch(String branch) {
        this.branch = branch;
    }
}
