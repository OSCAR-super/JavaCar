package com.car.demo.service.impl;


import com.car.demo.dao.entity.User;
import com.car.demo.dao.mapper.UserMapper;
import com.car.demo.service.UserService;
import com.car.demo.dao.entity.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void signIn(String openId) {
        userMapper.signIn(openId);
    }

    @Override
    public void bindingPlate(String openId, String plateNumber) {
        userMapper.bindingPlate(openId,plateNumber);
    }

    @Override
    public List<String> verifyPlate(String plateNumber) {
        return userMapper.verifyPlate(plateNumber);
    }

    @Override
    public User getUser(String openId) {
        return userMapper.getUser(openId);
    }

    @Override
    public List<String> getPlate(String openId) {
        return userMapper.getPlate(openId);
    }

    @Override
    public void setIndex(String openId, String sex,String phoneNumber, String birthday, String contactInformation, String abstractPersion) {
        userMapper.setIndex(openId,sex,phoneNumber,birthday,contactInformation,abstractPersion);
    }

    @Override
    public void creatForum(String forumId, String title, String category, String content, String fileName, String openId) {
        userMapper.creatForum(forumId,title,category,content,fileName,openId);
    }

    @Override
    public Forum getForum(String forumId) {
        return userMapper.getForum(forumId);
    }

    @Override
    public List<Forum> getRandomForum() {
        return userMapper.getRandomForum();
    }

    @Override
    public void deleteForum(String forumId) {
        userMapper.deleteForum(forumId);
    }

    @Override
    public void deletePlate(String openId, String plateNumber) {
        userMapper.deletePlate(openId,plateNumber);
    }

    @Override
    public List<Forum> getMyForum(String openId) {
        return userMapper.getMyForum(openId);
    }

    @Override
    public void setbinding(String openId, String openIdOther) {
        userMapper.setbinding(openId,openIdOther);
    }

    @Override
    public String ybinding(String openId, String openIdOther) {
        return null;
    }

    @Override
    public List<String> getbinding(String openId) {
        return userMapper.getbinding(openId);
    }

    @Override
    public void deletebinding(String openId, String openIdOther) {
        userMapper.deletebinding(openId,openIdOther);
    }

    @Override
    public List<String> getBindingM(String openId) {
        return userMapper.getbindingM(openId);
    }
}
