package com.valora.library.dbbundle.model;

import java.io.Serializable;
import java.util.Objects;

public class LanguageId implements Serializable {
    private String msgKey;
    private String locale;

    public LanguageId() {
    }

    public LanguageId(String msgKey, String locale) {
        this.msgKey = msgKey;
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LanguageId that)) return false;
        return Objects.equals(msgKey, that.msgKey) && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgKey, locale);
    }
}