package com.toolkit.assetscan.dao.helper;

import com.toolkit.assetscan.bean.UserProps;
import com.toolkit.assetscan.dao.mybatis.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersManageHelper {
    private final UsersMapper usersMapper;

    @Autowired
    public UsersManageHelper(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    public List<UserProps> getAllUsers() {
        return usersMapper.allUsers();
    }

    public UserProps getUserByUuid(String uuid){
        return usersMapper.getUserByUuid(uuid);
    }

    public boolean addOneUser(UserProps userProps) {
        int rv = usersMapper.addUser(userProps);
        return ( (rv == 1) ? true : false );
    }

    public boolean updateUserByUuid(UserProps userProps) {
        int rv = usersMapper.updateUserByUuid(userProps);
        return ( (rv == 1) ? true : false );
    }
}
