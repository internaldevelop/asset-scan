package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.MailManageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/emails")
@Api(value = "10. 邮件管理接口", tags = "10-Mails Manager API")
public class EmailManageApi {
    private Logger logger = LoggerFactory.getLogger(UserManageApi.class);
    private final MailManageService mMailManageService;
    private final ResponseHelper responseHelper;
    @Autowired
    private SystemLogsHelper systemLogs;

    @Autowired
    public EmailManageApi(MailManageService mailManageService, ResponseHelper responseHelper) {
        this.mMailManageService = mailManageService;
        this.responseHelper = responseHelper;
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public @ResponseBody
    Object sendSimpleTextMail(@RequestParam("subject") String subject,
                              @RequestParam("content") String content,
                              @RequestParam("toWho") String toWho,
                              @RequestParam(value = "attachment", required = false) String attachmentPath) throws InterruptedException{
        boolean success = mMailManageService.sendSimpleTextMail(subject,content,toWho, attachmentPath);
        if (success) {
            // 系统日志
            systemLogs.logEvent(responseHelper.success(), "发送邮件", content);
            return responseHelper.success();
        } else {
            systemLogs.logEvent(responseHelper.error(ErrorCodeEnum.ERROR_SEND_MAIL), "发送邮件", content);
            return responseHelper.error(ErrorCodeEnum.ERROR_SEND_MAIL);
        }
    }
}
