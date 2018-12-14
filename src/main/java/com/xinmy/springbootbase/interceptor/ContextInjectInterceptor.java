/*
 * Copyright 2015-2020 reserved by k12.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xinmy.springbootbase.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinmy.springbootbase.act.vo.UserVo;
import com.xinmy.springbootbase.annotation.Unsecured;
import com.xinmy.springbootbase.context.Context;
import com.xinmy.springbootbase.context.ContextHolder;
import com.xinmy.springbootbase.context.ContextImpl;
import com.xinmy.springbootbase.helper.IDGenerator;
import com.xinmy.springbootbase.helper.Result;
import com.xinmy.springbootbase.helper.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ClassUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @desc
 */
public class ContextInjectInterceptor extends HandlerInterceptorAdapter {
    public static final String SWAGGER_TOKEN = "5ecbad631f524b84b0045179c6c5f50f@9085541458513100833";
    public static final String TOKEN = "token";
    private static final Logger LOG = LoggerFactory.getLogger("ContextInjectInterceptor");
    @Autowired
    private Environment env;
    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init() {
        // 启动线程
        executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "context-cleaner");
                thread.setDaemon(true);
                return thread;
            }
        });
        executorService.scheduleAtFixedRate(() -> {
            // 清除过期context
            try {
                long now = System.currentTimeMillis();
                long diff = Duration.ofHours(2).toMillis();
                Map<String, Context> contexts = ContextHolder.contexts();
                contexts.forEach((token, context) -> {
                    long time = context.getLastAccessTime().getTime();
                    if (Long.compare(now - time, diff) >= 0) {
                        ContextHolder.remove(token);
                    }
                });
            } catch (Exception e) {
                //
            }
        }, 5, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        if (null != executorService) {
            try {
                executorService.shutdownNow();
            } catch (Exception e) {
                //
            }
        }
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, //
                             final HttpServletResponse response, //
                             final Object handler) throws Exception {
        //
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "PUT, POST, GET, DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                "X-Requested-With, X-Access-Token, X-Upload-Auth-Token, Origin, Content-Type, Cookie, " + TOKEN);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, TOKEN + ", errorCode");
        //
        LOG.info("王和请，进来=====================");
        if (CorsUtils.isPreFlightRequest(request)) {
            LOG.info("对吼=====================2");
            return true;
        }
        //
        Context context = retrieveContext(request);
        //
        ((ContextImpl) context).setLastAccessTime(new Date());
        ContextHolder.bindContext(context);
        response.setHeader(TOKEN, context.currentToken());
        appendCookie4Token(context, response); // 写入cookie
        //
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Class<?> targetClass = AopUtils.getTargetClass(hm.getBean());
            if (targetClass.getPackage().getName().contains("swagger")) {
                return super.preHandle(request, response, handler);
            }

            if (BasicErrorController.class.isAssignableFrom(targetClass)) {
                return super.preHandle(request, response, handler);
            }

            Method method = hm.getMethod();
            //
            Unsecured unsecured = findAnnotation(targetClass, method, Unsecured.class);
            if (null != unsecured) { //
                return true;
            } else {
                //
                if (!context.isAuthorized()) {
                    mustLogined(request, response);
                    return false;
                }
            }
            //
        }
        //
        return true;
    }

    // 只有登录成功后才可访问
    private void mustLogined(HttpServletRequest request, HttpServletResponse response) {
        Context context = ContextHolder.currentContext();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.setHeader("errorCode", "401");
        if (LOG.isErrorEnabled()) {
            LOG.error("只有登录成功后才可访问{}", request.getRequestURI());
        }
        try (PrintWriter out = response.getWriter()) {
            out.write(JSON.toJSONString(new Result(false, "请登录", "只有登录成功后才可访问"), SerializerFeature.QuoteFieldNames,
                    SerializerFeature.BrowserCompatible, SerializerFeature.WriteEnumUsingToString));
        } catch (Exception e) {
            //
        }
    }

    public static Context retrieveContext(HttpServletRequest request) {
        Context context;
        String token = request.getHeader(TOKEN);
        if (StringUtils.isEmpty(token) || StringUtils.isEquals("null", token)) {
            token = fromCookies(request);
        }
        //
        if (StringUtils.isEmpty(token) || StringUtils.isEquals("null", token)) {
            //
            token = UUID.randomUUID().toString().replaceAll("-", "") + "@" + IDGenerator.generateIDStr();
            context = buildAnoymouseContext(token);
        } else {
            //
            context = ContextHolder.contextOf(token);
            if (null == context) {
                context = buildAnoymouseContext(token);
            }
        }
        return context;
    }

    private static String fromCookies(HttpServletRequest request) {
        String token = null;
        // 未登录则， 从cookie中尝试获取token
        final Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                final String name = cookie.getName();
                if (StringUtils.isEquals(TOKEN, name)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }

    private void appendCookie4Token(Context context, HttpServletResponse response) {
        String token = context.currentToken();
        Cookie cookie = new Cookie(TOKEN, token);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofHours(12).getSeconds());
        // 写回cookie
        response.addCookie(cookie);
        //
        if (LOG.isDebugEnabled()) {
            LOG.debug("token[{}]写回cookie", token);
        }
    }

    private static Context buildAnoymouseContext(String token) {
        Context context;//
        UserVo user = new UserVo();
        //user.setName("ANONYMOUS");
        context = new ContextImpl(token, user);
        ContextHolder.appendContext(context);
        if (LOG.isDebugEnabled()) {
            LOG.debug("当前用户未登录");
        }
        //
        return context;
    }

    private <A extends Annotation> A findAnnotation(final Class<?> targetClass, //
                                                    final Method method, //
                                                    final Class<A> annotationType) {
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationType);
        if ((null == annotation) && (specificMethod != method)) {
            annotation = AnnotationUtils.findAnnotation(method, annotationType);
        }
        return annotation;

    }

    @Override
    public void afterCompletion(final HttpServletRequest request, //
                                final HttpServletResponse response, //
                                final Object handler, final Exception ex) throws Exception {
        //
        ContextHolder.unbindContext();
        //
        super.afterCompletion(request, response, handler, ex);
    }

}
