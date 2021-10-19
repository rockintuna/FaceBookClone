package com.spring.clone.user;

import com.spring.clone.exception.BadArgumentException;

public enum Sex {
        MAN("man"),
        WOMAN("woman");

        private String lower;

        Sex(String lower) {
                this.lower = lower;
        }

        public static Sex typeOf(String lower) {
                if ( lower.equals("man") ) {
                        return Sex.MAN;
                } else if ( lower.equals("woman") ) {
                        return Sex.WOMAN;
                } else {
                        throw new BadArgumentException("올바른 성별이 아닙니다.");
                }
        }
}
