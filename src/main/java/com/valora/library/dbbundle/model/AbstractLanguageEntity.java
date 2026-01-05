package com.valora.library.dbbundle.model;

import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.io.Serializable;

@MappedSuperclass
@IdClass(LanguageId.class)
public abstract class AbstractLanguageEntity implements Serializable {
    @Id
    @Column(name = "msg_key")
    private String msgKey;

    @Id
    @Column(name = "locale")
    private String locale;

    @Column(name = "msg_value", length = 2000)
    private String msgValue;

    // --- Manuel Getter ve Setter Metodları ---
    public String getMsgKey() { return msgKey; }
    public void setMsgKey(String msgKey) { this.msgKey = msgKey; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public String getMsgValue() { return msgValue; }
    public void setMsgValue(String msgValue) { this.msgValue = msgValue; }
}