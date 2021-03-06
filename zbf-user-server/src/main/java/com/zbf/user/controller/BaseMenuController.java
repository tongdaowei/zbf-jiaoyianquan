package com.zbf.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zbf.common.entity.Dats;
import com.zbf.common.entity.ResponseResult;
import com.zbf.common.utils.UID;
import com.zbf.user.entity.BaseMenu;
import com.zbf.user.entity.BaseUser;
import com.zbf.user.service.IBaseMenuService;
import com.zbf.user.service.IBaseUserService;
import com.zbf.user.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tong
 * @since 2020-09-16
 */
@RestController
@RequestMapping("/menu")
public class BaseMenuController {
    @Autowired
    private IBaseMenuService iBaseMenuService;


    @Autowired
    private IBaseUserService iBaseUserService;

    @Autowired
    private MenuService menuService;


    @RequestMapping("listMenu")
    public List<Map<String,Object>> listMenu(Integer id){
        BaseUser byId = iBaseUserService.getById(id);
        HashMap<String, Object> userHash = new HashMap<>();
        userHash.put("loginName",byId.getLoginName());
        userHash.put("id",byId.getId());
        List<Map<String, Object>> list = menuService.getlistMenu(userHash);
        return list;
    }

    //列表
    @RequestMapping("list")
    public List<BaseMenu> list(){
        List<BaseMenu> list=iBaseMenuService.list();
        return list;
    }

    //菜单列表
    @RequestMapping("selectMenu")
    public List<BaseMenu> selectMenu(){
        List<BaseMenu> codes=iBaseMenuService.list(new QueryWrapper<BaseMenu>().eq("parentCode",0));
        codes.forEach(co->{
            List<BaseMenu> parentCodes=iBaseMenuService.list(new QueryWrapper<BaseMenu>().eq("parentCode",co.getCode()));
            parentCodes.forEach(pa->{
                pa.setList(iBaseMenuService.list(new QueryWrapper<BaseMenu>().eq("parentCode",pa.getCode())));
            });
            co.setList(parentCodes);
        });
        return codes;
    }

    /**
      *@Author tongdaowei
      *@Description //TODO
      *@Date 2020/9/17 0017 下午 3:46
      *@miaoshu   处理时间
    **/
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

    /**
     *@Author tongdaowei
     *@Description //TODO
     *@Date 2020/9/17 0017 下午 6:13
     *@Param [time]
     *@return java.time.LocalDateTime
     *@miaoshu date转LocalDateTime
     **/
    private LocalDateTime dateToLocalDateTime(Date time) {
        Instant it=time.toInstant();
        ZoneId zid=ZoneId.systemDefault();
        return LocalDateTime.ofInstant(it,zid);
    }

    /**
     *@Author tongdaowei
     *@Description //TODO
     *@Date 2020/9/17 0017 下午 3:50
     *@miaoshu   添加菜单
     **/
    @RequestMapping("addMenu")
    public boolean addMenu(@RequestBody BaseMenu baseMenu){
        System.err.println("添加数据"+baseMenu.toString());
        baseMenu.setCreateTime(dateToLocalDateTime(new Date()));
        baseMenu.setVersion(10);
        baseMenu.setId(UID.next());
        boolean save=false;
        save=iBaseMenuService.save(baseMenu);
        if(save){
            baseMenu.setCode(Long.valueOf(baseMenu.getId()));
            boolean b=iBaseMenuService.updateById(baseMenu);
        }
        return save;
    }

    /**
      *@Author tongdaowei
      *@Description //TODO
      *@Date 2020/9/17 0017 下午 3:48
      *@miaoshu   修改菜单
    **/

    @RequestMapping("updateMenu")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult updateMenu(@RequestBody BaseMenu baseMenu){
        ResponseResult responseResult=new ResponseResult();
        System.err.println("修改的数据是"+baseMenu.toString());
        boolean save =false;
        save=iBaseMenuService.updateById(baseMenu);
        responseResult.setResult(save);
        return responseResult;
    }
//    @RequestMapping("updateMenu")
//    public boolean updateMenu(@RequestBody BaseMenu baseMenu){
//        baseMenu.setId(UID.next());
//        System.err.println("修改数据"+baseMenu.toString());
//        boolean update=false;
//        update=iBaseMenuService.updateById(baseMenu);
//        return update;
//    }

    /**
      *@Author tongdaowei
      *@Description //TODO
      *@Date 2020/9/17 0017 下午 3:49
      *@miaoshu   删除菜单
    **/
    @RequestMapping("deleteMenu")
    public ResponseResult deleteMenu(@RequestBody BaseMenu baseMenu){
        System.err.println("删除的数据是"+baseMenu.toString());
        boolean b =false;
        b = iBaseMenuService.removeById(baseMenu.getId());
        ResponseResult responseResult=new ResponseResult();
        responseResult.setResult(b);
        return responseResult;
    }
//    @RequestMapping("deleteMenu")
//    public boolean deleteMenu(@RequestBody BaseMenu baseMenu){
//        System.err.println("删除数据"+baseMenu.toString());
//        return true;
//    }


    /**
      *@Author tongdaowei
      *@Description //TODO
      *@Date 2020/9/22 0022 下午 3:02
      *@Param []
      *@return com.zbf.common.entity.ResponseResult
      *@miaoshu  获取所有的菜单
    **/
    @RequestMapping("selallmenu")
    public ResponseResult  selallmenu(){
        ResponseResult responseResult=new ResponseResult();
        List<BaseMenu> codes=iBaseMenuService.list(new QueryWrapper<BaseMenu>().eq("parentCode",0));

        codes.forEach(co->{

            List<BaseMenu> parentCodes = iBaseMenuService.list(new QueryWrapper<BaseMenu>().eq("parentCode", co.getCode()));
            parentCodes.forEach(pa->{
                pa.setList(iBaseMenuService.list(new QueryWrapper<BaseMenu>().eq("parentCode",pa.getCode())));
            });
            co.setList(parentCodes);
        });
        responseResult.setCode(200);
        responseResult.setResult(codes);
        return responseResult;

    }


    /**
      *@Author tongdaowei
      *@Description //TODO
      *@Date 2020/9/22 0022 下午 3:02
      *@Param [rid]
      *@return com.zbf.common.entity.ResponseResult
      *@miaoshu   根据角色获取菜单
    **/
    @RequestMapping("gjmenuRid")
    public ResponseResult gjmenuRid(Long rid) {
        ResponseResult responseResult=new ResponseResult();
        List<BaseMenu> id = iBaseMenuService.list(new QueryWrapper<BaseMenu>().
                inSql("id", "SELECT menu_id FROM base_role_menu WHERE base_role_menu.role_id=" + rid));
        Set set=new HashSet<>();
        id.forEach(s->{
            set.add(s.getId());
        });
        responseResult.setResult(set);
        return responseResult;
    }



}
