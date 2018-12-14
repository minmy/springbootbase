/*
 * Copyright 2015-2020 reserved by jf61.com.
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
package com.xinmy.springbootbase;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.xinmy.springbootbase.interceptor.ContextInjectInterceptor;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc
 */
@Configuration
public class AppConfiguration extends WebMvcConfigurationSupport {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);
    @Autowired
    private WebMvcProperties webMvcProperties;
    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HibernateValidator.class)
    public ValidatorFactory hibernateVvalidatorFactory() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setProviderClass(HibernateValidator.class);
        return localValidatorFactoryBean;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.http.converters.preferred-json-mapper", havingValue = "fastjson",
            matchIfMissing = false)
    public HttpMessageConverter<Object> fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter4 converter = new FastJsonHttpMessageConverter4();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(mediaTypes);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat(webMvcProperties.getDateFormat());
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteMapNullValue);
        converter.setFastJsonConfig(fastJsonConfig);
        //
        return converter;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        super.addInterceptors(registry);
        //
        ContextInjectInterceptor interceptor = this.ctxInjector();
        InterceptorRegistration registration = registry.addInterceptor(interceptor);
        // 排除swagger相关路径
        registration.excludePathPatterns("swagger-ui.html", "swagger-resources/**", "/webjars/**");
        //
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/**");
        corsRegistration.allowedOrigins("*");
        corsRegistration.exposedHeaders(ContextInjectInterceptor.TOKEN, "errorCode");
        corsRegistration.allowedMethods("PUT", "POST", "GET", "DELETE", "OPTIONS");
        corsRegistration.allowCredentials(false);
        corsRegistration.maxAge(30 * 24 * 3600); // 30 days
        corsRegistration.allowedHeaders("X-Requested-With", "X-Access-Token", "X-Upload-Auth-Token", "Origin",
                "Content-Type", "Cookie", "user_type", ContextInjectInterceptor.TOKEN);
    }

    @Bean
    public ContextInjectInterceptor ctxInjector() {
        return new ContextInjectInterceptor();
    }

    @Configuration
    @Primary
    @EnableTransactionManagement(proxyTargetClass = false)
    protected static class TransactionManagementConfiguration {

    }
}
