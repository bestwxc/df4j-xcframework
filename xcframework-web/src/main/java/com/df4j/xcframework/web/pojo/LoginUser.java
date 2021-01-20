package com.df4j.xcframework.web.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 登陆用户的实体类
 */
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 7348345489641034456L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 登陆用户名
     */
    private String userName;

    /**
     * 手机号码
     */
    private String mobileNo;

    /**
     * email
     */
    private String email;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 部门名称
     */
    private String deptCode;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 来源类型
     */
    private Integer sourceType;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户角色
     */
    private List<String> roleCodes;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }
}
