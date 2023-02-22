package com.isikato.api.web;

import com.isikato.api.model.res.IsikatoErrorModel;
import com.isikato.api.util.RequestUtil;
import com.isikato.service.exceptions.CategoryNotFoundException;
import com.isikato.service.exceptions.ContentException;
import com.isikato.service.exceptions.EmployeeException;
import com.isikato.service.exceptions.FileNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public IsikatoErrorModel handleValidationProblems(MethodArgumentNotValidException ex, HttpServletRequest request){
        var msg = getFirstViolationMsg(ex);
        return IsikatoErrorModel.createWithCurrentTime(msg, RequestUtil.getPath(request));
    }


    @ExceptionHandler({EmployeeException.class, ContentException.class,
            CategoryNotFoundException.class, CategoryNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public IsikatoErrorModel handleBadRequestException(RuntimeException ex, HttpServletRequest request){
        var msg = ex.getMessage();
        return IsikatoErrorModel.createWithCurrentTime(msg, RequestUtil.getPath(request));
    }

    @ExceptionHandler({FileNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public IsikatoErrorModel handleEmployeeException(FileNotFoundException ex, HttpServletRequest request){
        var msg = ex.getMessage();
        return IsikatoErrorModel.createWithCurrentTime(msg, RequestUtil.getPath(request));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public IsikatoErrorModel handleEmployeeException(DataIntegrityViolationException ex, HttpServletRequest request){
        var msg = "username already exists";
        return IsikatoErrorModel.createWithCurrentTime(msg, RequestUtil.getPath(request));
    }



    private String getFirstViolationMsg(MethodArgumentNotValidException ex) {
        return ex
                .getAllErrors()
                .stream().findAny()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("");
    }




}
