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
package com.xinmy.springbootbase.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @desc
 */
public class IDGenerator {
    public static Logger LOGGER            = LoggerFactory.getLogger(IDGenerator.class);
    private static final    int        BITS_TO_MOVE      = 0x0c;
    private static final    int        NODE_BITS_TO_MOVE = 0x38;
    public static final     long       MASK_TIME         = 0xffffffffff0L;
    public static final     long       ID_MASK           = 0xffffL;
    private static final    long       TIME_ID_MASK      = 0xffffffffffff0000L;
    private static final AtomicLong counter           = new AtomicLong(0);
    private static          boolean    wrapped           = false;
    private static volatile long       tmMark;

    static {
        IDGenerator.refresh();
    }

    private IDGenerator() {
    }

    public static long generateID() {
        long idReturn = counter.incrementAndGet();

        if ((idReturn & IDGenerator.ID_MASK) == 0) {
            final long timePortion = idReturn & IDGenerator.TIME_ID_MASK;
            if (timePortion >= IDGenerator.newTM()) {
                wrapped = true;
            } else {
                tmMark = timePortion;
            }
        }

        if (wrapped) {
            throw new IllegalStateException(
                    "The IdGenerator is being overlaped, and it needs revision as the system generated more than " +
                            IDGenerator.ID_MASK + " ids per 16 milliseconds which exceeded the IDgenerator limit");
        }

        return idReturn;
    }

    public static String generateIDStr() {
        return IDGenerator.generateID() + "";
    }

    public synchronized static void refresh() {
        long oldTm = tmMark;
        long newTm = IDGenerator.newTM();

        while (newTm <= oldTm) {
            newTm = IDGenerator.newTM();
        }
        tmMark = newTm;
        counter.set(tmMark);
    }

    public static String currentInfo() {
        long currentCounter = counter.get();
        return "IdGenerator(tmMark=" + IDGenerator.hex(tmMark) + ", CurrentCounter = " + currentCounter +
                ", HexCurrentCounter = " + IDGenerator.hex(currentCounter) + ")";
    }

    private static long newTM() {
        return (EnviromentUtils.NODE_ID << IDGenerator.NODE_BITS_TO_MOVE) |
                ((System.currentTimeMillis() & IDGenerator.MASK_TIME) << IDGenerator.BITS_TO_MOVE);
    }

    private static String hex(final long x) {
        return String.format("%1$X", x);
    }

}
