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
package com.xinmy.springbootbase.service.impl;

import com.xinmy.springbootbase.context.Context;
import com.xinmy.springbootbase.repository.BaseRepository;
import com.xinmy.springbootbase.service.CommonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @desc
 */
public abstract class CommonServiceImpl<T, V, ID extends Serializable> implements CommonService<T, V, ID> {
    @Override
    public T save(final Context context, final T entity) {
        return this.currentJpaRepository().save(entity);
    }

    protected abstract BaseRepository<T, ID> currentJpaRepository();

    @Override
    public T findOne(final ID id) {
        return this.currentJpaRepository().findOne(id);
    }

    @Override
    public T delete(final Context context, final ID id) {
        T entity = this.findOne(id);
        if (null != entity) {
            this.currentJpaRepository().delete(entity);
        }
        return entity;
    }

    @Override
    public List<T> findAll() {
        List<T> list = currentJpaRepository().findAll();
        if (null == list) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public Page<T> findByPage(Context context, V example, Pageable pageable) {
        return currentJpaRepository().findAll(pageable);
    }
}
