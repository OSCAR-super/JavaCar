package com.car.demo.dao.entity;

public class User {
    private String openId;
    private String sex;
    private String phoneNumber;
    private String birthday;
    private String contactInformation;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getAbstractPersion() {
        return abstractPersion;
    }

    public void setAbstractPersion(String abstractPersion) {
        this.abstractPersion = abstractPersion;
    }

    private String abstractPersion;
}
