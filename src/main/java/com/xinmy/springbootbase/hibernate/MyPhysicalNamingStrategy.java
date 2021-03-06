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
package com.xinmy.springbootbase.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

/**
 *
 * @desc
 */
public class MyPhysicalNamingStrategy extends SpringPhysicalNamingStrategy {
    //
    public static final String PREFIX = "xin_";

    //
    @Override
    public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
        final Identifier identifier = super.toPhysicalTableName(name, jdbcEnvironment);
        return new Identifier(PREFIX + identifier.getText(), identifier.isQuoted());
    }

}
