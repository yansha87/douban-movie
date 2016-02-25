package com.demon.doubanmovies.db.bean;

import java.util.List;

// 作品
public class WorksEntity {

    public SubjectEntity subject;
    public List<String> roles;
    public SubjectEntity getSubject() {
        return subject;
    }

    public void setSubject(SubjectEntity subject) {
        this.subject = subject;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


}
