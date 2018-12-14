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
package com.xinmy.springbootbase.service;

import com.xinmy.springbootbase.context.Context;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;


/**
 * @desc
 */
@Transactional(readOnly = true)
public interface CommonService<T, V, ID extends Serializable> {
    @Transactional
    T save(Context context, T entity);

    T findOne(ID id);

    @Transactional
    T delete(Context context, ID id);

    Page<T> findByPage(Context context, V example, Pageable pageable);

    List<T> findAll();

}
