package com.toolkit.assetscan.dao.helper;

import com.toolkit.assetscan.bean.po.UserPo;
import com.toolkit.assetscan.dao.mybatis.UsersMapper;
import com.toolkit.assetscan.global.params.SystemParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersManageHelper {
    private final UsersMapper usersMapper;

    @Autowired
    public UsersManageHelper(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    public boolean isAccountExist(String account) {
        int count = usersMapper.getExistAccountCount(account);
        return ( (count > 0) ? true : false);
    }

    public boolean addUser(UserPo userPo) {
        int rv = usersMapper.addUser(userPo);
        return (rv > 0);
    }

    public boolean deleteUser(String userUuid) {
        int rv = usersMapper.deleteUser(userUuid);
        return (rv > 0);
    }

    public boolean updateUserByUuid(UserPo userPo) {
        int rv = usersMapper.updateUserByUuid(userPo);
        return (rv > 0);
    }

    public boolean changePassword(String userUuid, String newPwd) {
        int rv = usersMapper.changePassword(userUuid, newPwd);
        return (rv > 0);
    }

    public boolean resetPasswordRAT(String userUuid) {
        int rv = usersMapper.updateRAT(userUuid, SystemParams.USER_PWD_MAT);
        return (rv > 0);
    }

    public boolean decreasePasswordRAT(String userUuid, int currentRAT) {
        // RAT已经减到0，直接返回成功
        if (currentRAT <= 0)
            return true;

        int rv = usersMapper.updateRAT(userUuid, currentRAT - 1);
        return (rv > 0);
    }
}
