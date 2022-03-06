package com.cy.store.controller;

import com.cy.store.controller.ex.*;
import com.cy.store.entity.User;
import com.cy.store.service.IUserService;
import com.cy.store.service.ex.InsertException;
import com.cy.store.service.ex.UsernameDuplicateException;
import com.cy.store.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController extends BaseController{

    @Autowired
    private IUserService userService;

    @RequestMapping("reg")
    public JsonResult<Void> reg(User user) {
        // 调用业务对象执行注册
        userService.reg(user);
        // 返回
        return new JsonResult<Void>(OK);
    }

    @RequestMapping("index")
    public JsonResult<Void> index(User user) {
        // 调用业务对象执行注册

        return new JsonResult<Void>(OK);
    }

    @RequestMapping("login")
    public JsonResult<User> login(String username,
                                  String password,
                                  HttpSession session) {
        User user = userService.login(username, password);
        session.setAttribute("uid",user.getUid());
        session.setAttribute("username",user.getUsername());
        return new JsonResult<User>(OK,user);
    }


    @RequestMapping("/change_password")
    public JsonResult<Void> changePassword(String oldPassword,
                                           String newPassword,
                                           HttpSession session){
        Integer uid = getUidFromSession(session);
        String username = getUsernameFromSession(session);
        userService.changePassword(uid,username,oldPassword,newPassword);
        return new JsonResult<Void>(OK);
    }


    @RequestMapping("get_by_uid")
    public JsonResult<User> getByUid(HttpSession session){
        User data = userService.getByUid(getUidFromSession(session));
        return new JsonResult<User>(OK,data);
    }

    @RequestMapping("change_info")
    public  JsonResult<Void> changeInfo(User user,
                                        HttpSession session){
        userService.changeInfo(getUidFromSession(session),getUsernameFromSession(session),user);
        return new JsonResult<Void>(OK);
    }

    /** 头像文件大小的上限值(10MB) */
    @Value("${max_size}")
    public static final int AVATAR_MAX_SIZE = 10 * 1024 * 1024;
    /** 允许上传的头像的文件类型 */
    public static final List<String> AVATAR_TYPES = new ArrayList<String>();

    /** 初始化允许上传的头像的文件类型 */
    static {
        AVATAR_TYPES.add("image/jpeg");
        AVATAR_TYPES.add("image/png");
        AVATAR_TYPES.add("image/bmp");
        AVATAR_TYPES.add("image/gif");
    }

    @PostMapping("change_avatar")
    public JsonResult<String> changeAvatar(HttpSession session,
                                           @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileEmptyException("上传的头像文件不允许为空");
        }

        if (file.getSize() > AVATAR_MAX_SIZE) { // getSize()：返回文件的大小，以字节为单位
            throw new FileSizeException("不允许上传超过" + (AVATAR_MAX_SIZE / 1024) + "KB的头像文件");
        }

        String contentType = file.getContentType();
        if (!AVATAR_TYPES.contains(contentType)) {
            throw new FileTypeException("不支持使用该类型的文件作为头像，允许的文件类型：" + AVATAR_TYPES);
        }

        // 获取当前项目的绝对磁盘路径
        String parent = session.getServletContext().getRealPath("upload");
        File dir = new File(parent);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存的头像文件的文件名,文件名不能重复，重复会被覆盖，所以用随机生成确保唯一
        String suffix = "";
        String originalFilename = file.getOriginalFilename();
        int beginIndex = originalFilename.lastIndexOf(".");
        if (beginIndex > 0) {
            suffix = originalFilename.substring(beginIndex);
        }
        String filename = UUID.randomUUID().toString() + suffix;

        File dest = new File(dir, filename);
        try {
            file.transferTo(dest);//将file文件写入到dest，前提条件，这两个文件后缀要相同
        } catch (IllegalStateException e) {
            throw new FileStateException(" ，可能文件已被移动或删除");
        } catch (IOException e) {
            throw new FileUploadIOException("上传文件时读写错误，请稍后重新尝试");
        }
        String avatar = "/upload/" + filename;
        Integer uid = getUidFromSession(session);
        String username = getUsernameFromSession(session);
        userService.changeAvatar(uid, username, avatar);

        return new JsonResult<String>(OK, avatar);
    }
}
