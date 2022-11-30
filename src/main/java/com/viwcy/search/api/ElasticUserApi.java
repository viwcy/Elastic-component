package com.viwcy.search.api;

import com.viwcy.search.common.BaseController;
import com.viwcy.search.common.ResultEntity;
import com.viwcy.search.entity.ElasticUser;
import com.viwcy.search.param.UserSearchParam;
import com.viwcy.search.service.ElasticUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@RestController
@RequestMapping("/user")
public class ElasticUserApi extends BaseController {

    @Resource
    private ElasticUserService elasticUserService;

    @PostMapping("/page")
    public ResultEntity page(@RequestBody UserSearchParam param) {

        return ResultEntity.success(elasticUserService.page(param));
    }

    @PostMapping("/count")
    public ResultEntity count(@RequestBody UserSearchParam param) {

        return ResultEntity.success(elasticUserService.count(param));
    }

    @PostMapping("/save")
    public ResultEntity save(@RequestBody ElasticUser param) {

        elasticUserService.save(param);
        return ResultEntity.success();
    }

    @PostMapping("/saveBatch")
    public ResultEntity saveBatch(@RequestBody List<ElasticUser> params) {

        elasticUserService.saveBatch(params);
        return ResultEntity.success();
    }

    @PostMapping("/queryById")
    public ResultEntity query(@RequestParam Long id) {

        return ResultEntity.success(elasticUserService.queryById(id));
    }

    @PostMapping("/update")
    public ResultEntity update(@RequestBody ElasticUser param) {

        elasticUserService.update(param);
        return ResultEntity.success();
    }

    @PostMapping("/delete")
    public ResultEntity delete(@RequestParam Long id) {

        elasticUserService.delete(id);
        return ResultEntity.success();
    }
}
