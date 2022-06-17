package com.kuangheng.cloud.handler;

import com.kuangheng.cloud.constant.I18nConstantCode;
import com.kuangheng.cloud.exception.BusinessException;
import com.kuangheng.cloud.util.MessageUtils;
import com.wisdge.cloud.dto.ApiResult;
import com.wisdge.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 无法解析参数异常
     *
     * @param exception
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Object HttpMessageNotReadableHandler(HttpMessageNotReadableException exception) throws Exception {
        return ApiResult.fail(MessageUtils.getMessage(I18nConstantCode.PARAMS_GET_ERROR));
    }

    /**
     * 方法访问权限不足异常
     *
     * @param exception
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public Object AccessDeniedExceptionHandler(AccessDeniedException exception) throws Exception {
        return ApiResult.forbidden(MessageUtils.getMessage(I18nConstantCode.FORBIDDEN));
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public Object NoHandlerFoundExceptionHandler(NoHandlerFoundException exception) throws Exception {
        return ApiResult.notFound(MessageUtils.getMessage(I18nConstantCode.NOT_FOUND));
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public Object AuthenticationExceptionHandler(AuthenticationException e) {
        return ApiResult.forbidden(MessageUtils.getMessage(I18nConstantCode.UNAUTHORIZED));
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public Object DuplicateKeyExceptionHandler(DuplicateKeyException e) {
        return ApiResult.internalError(MessageUtils.getMessage(I18nConstantCode.DB_RECORD_EXISTS));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public Object DataIntegrityViolationException(DataIntegrityViolationException e) {
        return ApiResult.internalError(MessageUtils.getMessage(I18nConstantCode.INTERNAL_SERVER_ERROR));
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ApiResult exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(e.getMessage());
    }

    /**
     * 处理业务异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(), e);
        String msg = e.getMessage();
        if (StringUtils.isNotBlank(msg)) {
            return ApiResult.fail(msg);
        }
        return ApiResult.fail(MessageUtils.getMessage(e.getCode(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public ApiResult bindExceptionHandler(final BindException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ApiResult.fail(message);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult handler(final ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        return ApiResult.fail(message);
    }

    /**
     * 参数未通过验证异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handler(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        return ApiResult.fail(message);
    }

}
