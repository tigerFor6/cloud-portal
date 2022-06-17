package com.kuangheng.cloud.activity.controller;

import com.wisdge.cloud.controller.BaseController;
import com.wisdge.cloud.dto.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.fs.FileUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.guide.WxMpGuideWordMaterialInfoList;
import me.chanjar.weixin.mp.bean.material.*;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.util.requestexecuter.material.MaterialNewsInfoRequestExecutor;
import me.chanjar.weixin.mp.util.requestexecuter.material.MaterialVoiceAndImageDownloadRequestExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static me.chanjar.weixin.mp.enums.WxMpApiUrl.Material.*;

/**
 * 微信公众号
 *
 * @author tiger
 * @date 2021-05-13 16:44:50
 */
@Api(tags = "微信公众号")
@RestController
@RequestMapping("/event/wx")
public class WxMpController extends BaseController {

    @Autowired
    private WxMpService wxMpService;

    @PostMapping("/mediaImgUpload")
    @ApiOperation("mediaImgUpload")
    public ApiResult mediaImgUpload(@RequestParam(value = "file", required = true) MultipartFile file) throws WxErrorException, IOException {
        InputStream inputStream = file.getInputStream();
        WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService()
                .mediaUpload("image", "jpeg", inputStream);
        return ApiResult.ok("", wxMediaUploadResult);
    }

    @PostMapping("/materialFileUpload")
    @ApiOperation("新增永久图文素材")
    public ApiResult materialFileUpload() throws WxErrorException {
        // mediaType 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
        // 单图文消息
        WxMpMaterialNews wxMpMaterialNewsSingle = new WxMpMaterialNews();
        WxMpNewsArticle article = new WxMpNewsArticle();
        article.setAuthor("author");
        article.setThumbMediaId("");
        article.setTitle("single title");
        article.setContent("single content");
        article.setContentSourceUrl("content url");
        article.setShowCoverPic(true);
        article.setDigest("single news");
        wxMpMaterialNewsSingle.addArticle(article);
        WxMpMaterialUploadResult resSingle = wxMpService.getMaterialService().materialNewsUpload(wxMpMaterialNewsSingle);
        return ApiResult.ok("", resSingle);
    }

//    @PostMapping("/materialFileUpload")
//    @ApiOperation("新增其他类型永久素材")
//    public ApiResult materialFileUpload(@RequestParam(value = "file", required = true) MultipartFile file, String mediaType, String fileType) throws WxErrorException, IOException {
//        // mediaType 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
//        try (InputStream inputStream = file.getInputStream()) {
//            File tempFile = FileUtils.createTmpFile(inputStream,
//                    UUID.randomUUID().toString(), fileType);
//            WxMpMaterial wxMaterial = new WxMpMaterial();
//            wxMaterial.setFile(tempFile);
//            wxMaterial.setName(tempFile.getName());
//            WxMpMaterialUploadResult res = wxMpService.getMaterialService()
//                    .materialFileUpload(mediaType, wxMaterial);
//            if (WxConsts.MediaFileType.IMAGE.equals(mediaType)
//                    || WxConsts.MediaFileType.THUMB.equals(mediaType)) {
//                res.getUrl();
//            }
//            return ApiResult.ok("", res);
//        }
//    }

    @GetMapping("/materialImageOrVoiceDownload")
    @ApiOperation("获取声音或者图片永久素材")
    public InputStream materialImageOrVoiceDownload() throws WxErrorException {
        String mediaId = request.getParameter("mediaId");
        InputStream inputStream = wxMpService.execute(MaterialVoiceAndImageDownloadRequestExecutor
                        .create(this.wxMpService.getRequestHttp(), this.wxMpService.getWxMpConfigStorage().getTmpDirFile()),
                MATERIAL_GET_URL, mediaId);
        return inputStream;
    }

    @GetMapping("/materialNewsInfo")
    @ApiOperation("获取图文永久素材的信息")
    public ApiResult materialNewsInfo() throws WxErrorException {
        String mediaId = request.getParameter("mediaId");
        WxMpMaterialNews wxMpMaterialNews = wxMpService.execute(MaterialNewsInfoRequestExecutor.create(this.wxMpService.getRequestHttp()),
                MATERIAL_GET_URL, mediaId);
        return ApiResult.ok("图文永久素材", wxMpMaterialNews);
    }

    @GetMapping("/getGuideImageMaterial")
    @ApiOperation("获取素材列表")
    public ApiResult getGuideImageMaterial() throws WxErrorException {
//        type  素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
//        offset 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
//        count   返回素材的数量，取值在1到20之间
//        return 素材列表
        String type = request.getParameter("type");
        WxMpMaterialFileBatchGetResult wxMpMaterialImageBatchGetResult = wxMpService.getMaterialService().materialFileBatchGet(type, 0, 20);
        return ApiResult.ok("素材列表", wxMpMaterialImageBatchGetResult);
    }


    @PostMapping("/massOpenIdsMessageSend")
    @ApiOperation("发送群发消息")
    public ApiResult massOpenIdsMessageSend() throws WxErrorException {
        WxMpMassOpenIdsMessage massMessage = new WxMpMassOpenIdsMessage();
        massMessage.setMsgType(WxConsts.MassMsgType.TEXT);
        massMessage.setContent("测试群发消息");
        List<String> toUsers = new ArrayList<String>();
        toUsers.add("oqbVT1utNjU3RDTfzX1EH8QKKC-k");
        toUsers.add("oqbVT1qSQJcb15rwRnlAgdctLWO4");
        massMessage.setToUsers(toUsers); // Openid
        WxMpMassSendResult massResult = wxMpService.getMassMessageService().massOpenIdsMessageSend(massMessage);
        return ApiResult.ok("发送群发消息", massResult);
    }
}
