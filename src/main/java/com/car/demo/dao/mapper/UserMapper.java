package com.car.demo.dao.mapper;


import com.car.demo.dao.entity.Forum;
import com.car.demo.dao.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    void signIn(@Param("openId") String openId);

    void bindingPlate(@Param("openId")String openId, @Param("plateNumber")String plateNumber);

    List<String> verifyPlate(@Param("plateNumber")String plateNumber);

    User getUser(@Param("openId")String openId);

    List<String> getPlate(@Param("openId")String openId);

    void setIndex(@Param("openId")String openId,@Param("sex") String sex,@Param("phoneNumber") String phoneNumber,@Param("birthday") String birthday, @Param("contactInformation")String contactInformation,@Param("abstractPersion") String abstractPersion);

    void creatForum(@Param("forumId")String forumId,@Param("title") String title,@Param("category") String category,@Param("content") String content,@Param("fileName") String fileName,@Param("openId") String openId);

    Forum getForum(@Param("forumId")String forumId);

    List<Forum> getRandomForum();

    void deleteForum(@Param("forumId")String forumId);

    void deletePlate(@Param("openId")String openId,@Param("plateNumber") String plateNumber);

    List<Forum> getMyForum(@Param("openId")String openId);

    void setbinding(@Param("openIdM")String openId, @Param("openIdS")String openIdOther);

    List<String> getbinding(@Param("openIdM")String openId);

    void deletebinding(@Param("openIdM")String openId, @Param("openIdS")String openIdOther);

    List<String> getbindingM(@Param("openIdS")String openId);
}
