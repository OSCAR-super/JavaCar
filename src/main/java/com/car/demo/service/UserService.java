package com.car.demo.service;



import com.car.demo.dao.entity.Forum;
import com.car.demo.dao.entity.User;

import java.util.List;

public interface UserService {

    void signIn(String openId);

    void bindingPlate(String openId, String plateNumber);

    List<String> verifyPlate(String plateNumber);

    User getUser(String openId);

    List<String> getPlate(String openId);

    void setIndex(String openId, String sex,String phoneNumber, String birthday, String contactInformation, String abstractPersion);

    void creatForum(String forumId, String title, String category, String content, String fileName, String openId);

    Forum getForum(String forumId);

    List<Forum> getRandomForum();

    void deleteForum(String forumId);

    void deletePlate(String openId, String plateNumber);

    List<Forum> getMyForum(String openId);

    void setbinding(String openId, String openIdOther);

    String ybinding(String openId, String openIdOther);

    List<String> getbinding(String openId);

    void deletebinding(String openId, String openIdOther);

    List<String> getBindingM(String openId);
}
