package com.toolkit.assetscan.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/tasks")
@Api(value = "03. 任务管理接口", tags = "03-Tasks Manager API")
public class TaskManageApi {
    private Logger logger = LoggerFactory.getLogger(TaskManageApi.class);

    /**
     * 3.1 添加一个新的任务
     * @param task task 参数
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 任务名和任务的 UUID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addTask(@ModelAttribute Object task, BindingResult bindingResult) {
        return null;
    }

    /**
     * 3.2 移除一个任务
     * @param taskUuid 任务的 UUID
     * @return payload: 无
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody
    Object removeTask(@RequestParam("uuid") String taskUuid) {
        return null;
    }

    /**
     * 3.3 获取所有的任务
     * @return payload: 所有任务的数组 （JSON 格式）
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllTasks() {
        return null;
    }

    /**
     * 3.4 根据指定的 UUID 获取一条任务参数
     * @param taskUuid 任务的 UUID
     * @return payload: 任务参数（任务记录及外键字段的含义）
     */
    @RequestMapping(value = "/get-task", method = RequestMethod.GET)
    public @ResponseBody
    Object getTask(@RequestParam("uuid") String taskUuid) {
        return null;
    }

    /**
     * 3.5 更新一条记录
     * @param task 任务参数
     * @return payload: 任务名和任务的 UUID
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object  updateTask(@ModelAttribute Object task) {
        return null;
    }

    /**
     * 3.6 执行一条任务，将执行结果保存到数据库中，并通过消息队列通知前端应用
     * @param taskUuid 任务的 UUID
     * @return payload: 执行结果的 UUID
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public @ResponseBody
    Object  executeTask(@RequestParam("uuid") String taskUuid) {
        return null;
    }

}
