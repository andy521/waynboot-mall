package com.wayn.admin.api.controller.tool;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.wayn.common.base.controller.BaseController;
import com.wayn.common.core.domain.tool.QiniuConfig;
import com.wayn.common.core.domain.tool.QiniuContent;
import com.wayn.common.core.service.tool.IQiniuConfigService;
import com.wayn.common.core.service.tool.IQiniuContentService;
import com.wayn.common.util.R;
import com.wayn.common.util.file.QiniuUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("tool/qiniu")
public class QiniuController extends BaseController {

    @Autowired
    private IQiniuConfigService iQiniuConfigService;

    @Autowired
    private IQiniuContentService iQiniuContentService;

    @GetMapping("/list")
    public R list(QiniuContent qiniuContent) {
        Page<QiniuContent> page = getPage();
        return R.success().add("page", iQiniuContentService.listPage(page, qiniuContent));
    }

    @GetMapping("config")
    public R info() {
        return R.success().add("data", iQiniuConfigService.getById(1));
    }

    @PutMapping("config")
    public R update(@Valid @RequestBody QiniuConfig qiniuConfig) {
        qiniuConfig.setId(1L);
        iQiniuConfigService.updateById(qiniuConfig);
        return R.success("修改成功");
    }

    @PostMapping("upload")
    public R upload(@RequestParam MultipartFile file) throws IOException {
        QiniuConfig qiniuConfig = iQiniuConfigService.getById(1);
        if (qiniuConfig == null) {
            return R.error("七牛云配置不存在");
        }
        if (StringUtils.isEmpty(qiniuConfig.getAccessKey())) {
            return R.error("七牛云配置错误");
        }
        QiniuContent qiniuContent = iQiniuContentService.upload(file, qiniuConfig);
        return R.result(iQiniuContentService.save(qiniuContent)).add("id", qiniuContent.getContentId()).add("fileUrl", qiniuContent.getUrl());
    }

    @GetMapping("download/{id}")
    public R download(@PathVariable Long id) {
        QiniuConfig qiniuConfig = iQiniuConfigService.getById(1);
        if (qiniuConfig == null) {
            return R.error("七牛云配置不存在");
        }
        if (StringUtils.isEmpty(qiniuConfig.getAccessKey())) {
            return R.error("七牛云配置错误");
        }
        return R.success().add("url", iQiniuContentService.download(id, qiniuConfig));
    }

    @GetMapping("syncQiniu")
    public R syncQiniu() {
        QiniuConfig config = iQiniuConfigService.getById(1);
        if (config == null) {
            return R.error("七牛云配置不存在");
        }
        if (StringUtils.isEmpty(config.getAccessKey())) {
            return R.error("七牛云配置错误");
        }
        iQiniuContentService.syncQiniu(config);
        return R.success();
    }

    @DeleteMapping(value = "{id}")
    public R delete(@PathVariable Long id) throws QiniuException {
        QiniuContent content = iQiniuContentService.getById(id);
        QiniuConfig config = iQiniuConfigService.getById(1);
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiniuUtil.getRegion(config.getRegion()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        Response response = bucketManager.delete(content.getBucket(), content.getName() + "." + content.getSuffix());
        if (response.isOK()) {
            iQiniuContentService.removeById(content);
        }
        return R.success();
    }
}
